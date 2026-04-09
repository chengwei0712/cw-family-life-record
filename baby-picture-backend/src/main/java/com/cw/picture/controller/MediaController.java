package com.cw.picture.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cw.picture.entity.PictureInfo;
import com.cw.picture.mapper.PictureMapper;
import com.cw.picture.response.CommonResponse;
import com.cw.picture.response.PictureVO;
import com.cw.picture.util.MinioUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

/**
 * 媒体文件控制器
 *
 * @author chengwei
 * @since 2025.12.30
 */
@Slf4j
@RestController
@RequestMapping("/media")
public class MediaController {

    // 图片限制
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp");
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
    
    // 视频限制
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(".mp4", ".mov", ".avi", ".mkv", ".webm");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/quicktime", "video/x-msvideo", "video/x-matroska", "video/webm");

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private MinioUtil minioUtil;

    /**
     * 分页获取媒体列表
     */
    @GetMapping("/list")
    public CommonResponse<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        
        Page<PictureInfo> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<PictureInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PictureInfo::getIsDeleted, 0);
        wrapper.eq(PictureInfo::getStatus, 0);
        
        // 按类型筛选
        if (StringUtils.isNotBlank(type)) {
            if ("photo".equals(type)) {
                wrapper.likeRight(PictureInfo::getFileType, "image/");
            } else if ("video".equals(type)) {
                wrapper.likeRight(PictureInfo::getFileType, "video/");
            }
        }
        
        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(PictureInfo::getFileName, keyword);
        }
        
        wrapper.orderByDesc(PictureInfo::getCreateTime);
        
        Page<PictureInfo> result = pictureMapper.selectPage(pageParam, wrapper);
        
        // 转换为VO，确保ID为字符串格式
        List<Map<String, Object>> voList = new ArrayList<>();
        for (PictureInfo info : result.getRecords()) {
            Map<String, Object> vo = new HashMap<>();
            vo.put("id", String.valueOf(info.getId())); // 转为字符串，避免前端精度丢失
            vo.put("fileName", info.getFileName());
            vo.put("fileSize", info.getFileSize());
            vo.put("fileType", info.getFileType());
            // 获取访问URL
            try {
                vo.put("filePath", minioUtil.getFileUrl(info.getStoreName()));
            } catch (Exception e) {
                log.error("获取文件URL失败: {}", info.getStoreName());
                vo.put("filePath", "");
            }
            voList.add(vo);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);
        
        return CommonResponse.success(response);
    }

    /**
     * 获取单个媒体详情
     */
    @GetMapping("/{id}")
    public CommonResponse<?> getDetail(@PathVariable Long id) {
        PictureInfo info = pictureMapper.selectById(id);
        if (info == null || info.getIsDeleted() == 1) {
            return CommonResponse.failed("文件不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", String.valueOf(info.getId())); // 转为字符串
        result.put("fileName", info.getFileName());
        result.put("fileSize", info.getFileSize());
        result.put("fileType", info.getFileType());
        try {
            result.put("filePath", minioUtil.getFileUrl(info.getStoreName()));
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            result.put("filePath", "");
        }
        
        return CommonResponse.success(result);
    }

    /**
     * 上传媒体文件（支持图片和视频）
     */
    @PostMapping("/upload")
    public CommonResponse<?> upload(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return CommonResponse.failed("请选择要上传的文件");
        }

        List<PictureInfo> successList = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            String lowerExt = extension.toLowerCase();
            
            try {
                // 判断文件类型
                String mediaType = null;
                String contentType = file.getContentType();
                
                // 先通过扩展名判断
                if (IMAGE_EXTENSIONS.contains(lowerExt)) {
                    mediaType = "photo";
                } else if (VIDEO_EXTENSIONS.contains(lowerExt)) {
                    mediaType = "video";
                }
                
                // 如果扩展名没匹配，尝试通过contentType判断
                if (mediaType == null && contentType != null) {
                    if (contentType.startsWith("image/")) {
                        mediaType = "photo";
                    } else if (contentType.startsWith("video/")) {
                        mediaType = "video";
                    }
                }
                
                if (mediaType == null) {
                    failedFiles.add(originalName + " (不支持的文件类型: " + extension + ")");
                    log.warn("不支持的文件类型: {} - {}", originalName, contentType);
                    continue;
                }

                // 验证文件大小
                long maxSize = "video".equals(mediaType) ? MAX_VIDEO_SIZE : MAX_IMAGE_SIZE;
                if (file.getSize() > maxSize) {
                    failedFiles.add(originalName + " (文件过大，最大" + (maxSize / 1024 / 1024) + "MB)");
                    continue;
                }

                // 生成唯一文件名
                String uniqueName = UUID.randomUUID().toString() + extension;
                
                // 上传到MinIO
                try (InputStream inputStream = file.getInputStream()) {
                    minioUtil.upload(file, uniqueName);
                }
                
                // 确定contentType
                String finalContentType = contentType;
                if (finalContentType == null || finalContentType.isEmpty()) {
                    finalContentType = "video".equals(mediaType) ? "video/mp4" : "image/jpeg";
                }

                // 保存记录
                PictureInfo info = PictureInfo.builder()
                        .fileName(originalName)
                        .filePath(minioUtil.getFileUrl(uniqueName))
                        .fileSize(formatFileSize(file.getSize()))
                        .fileType(finalContentType)
                        .storeName(uniqueName)
                        .status(0)
                        .isDeleted(0)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                
                pictureMapper.insert(info);
                successList.add(info);
                
                log.info("上传成功: {} ({}) -> {}", originalName, mediaType, uniqueName);
                
            } catch (Exception e) {
                log.error("上传失败: {}", originalName, e);
                failedFiles.add(originalName + " (" + e.getMessage() + ")");
            }
        }

        // 构建成功上传的文件列表（包含详细信息，ID转为字符串避免前端精度丢失）
        List<Map<String, Object>> successFileList = new ArrayList<>();
        for (PictureInfo info : successList) {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", String.valueOf(info.getId())); // 转为字符串
            fileInfo.put("fileName", info.getFileName());
            fileInfo.put("filePath", minioUtil.getFileUrl(info.getStoreName()));
            fileInfo.put("fileSize", info.getFileSize());
            fileInfo.put("fileType", info.getFileType());
            successFileList.add(fileInfo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successList.size());
        result.put("failedCount", failedFiles.size());
        result.put("failedFiles", failedFiles);
        result.put("successList", successFileList);

        if (failedFiles.isEmpty()) {
            return CommonResponse.success(result, "上传成功");
        } else if (successList.isEmpty()) {
            return CommonResponse.failed("上传失败: " + String.join(", ", failedFiles));
        } else {
            return CommonResponse.success(result, "部分文件上传成功");
        }
    }

    /**
     * 删除媒体文件
     */
    @DeleteMapping("/{id}")
    public CommonResponse<?> delete(@PathVariable Long id) {
        PictureInfo info = pictureMapper.selectById(id);
        if (info == null) {
            return CommonResponse.failed("文件不存在");
        }
        
        // 软删除
        info.setIsDeleted(1);
        info.setUpdateTime(new Date());
        pictureMapper.updateById(info);
        
        return CommonResponse.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete")
    public CommonResponse<?> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return CommonResponse.failed("请选择要删除的文件");
        }
        
        for (Long id : ids) {
            PictureInfo info = pictureMapper.selectById(id);
            if (info != null) {
                info.setIsDeleted(1);
                info.setUpdateTime(new Date());
                pictureMapper.updateById(info);
            }
        }
        
        return CommonResponse.success("删除成功");
    }

    /**
     * 获取媒体统计
     */
    @GetMapping("/stats")
    public CommonResponse<?> getStats() {
        LambdaQueryWrapper<PictureInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PictureInfo::getIsDeleted, 0);
        
        long total = pictureMapper.selectCount(wrapper);
        
        // 照片数量
        LambdaQueryWrapper<PictureInfo> photoWrapper = new LambdaQueryWrapper<>();
        photoWrapper.eq(PictureInfo::getIsDeleted, 0);
        photoWrapper.likeRight(PictureInfo::getFileType, "image/");
        long photoCount = pictureMapper.selectCount(photoWrapper);
        
        // 视频数量
        LambdaQueryWrapper<PictureInfo> videoWrapper = new LambdaQueryWrapper<>();
        videoWrapper.eq(PictureInfo::getIsDeleted, 0);
        videoWrapper.likeRight(PictureInfo::getFileType, "video/");
        long videoCount = pictureMapper.selectCount(videoWrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("photoCount", photoCount);
        stats.put("videoCount", videoCount);
        
        return CommonResponse.success(stats);
    }

    // ============ 辅助方法 ============

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}