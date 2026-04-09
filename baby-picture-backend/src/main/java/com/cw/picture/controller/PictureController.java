package com.cw.picture.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cw.picture.entity.PictureInfo;
import com.cw.picture.mapper.PictureMapper;
import com.cw.picture.request.PictureRequest;
import com.cw.picture.response.CommonResponse;
import com.cw.picture.response.PictureVO;
import com.cw.picture.util.MinioUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 图片管理控制器
 *
 * @author chengwei
 * @since 2025.12.30
 */
@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private MinioUtil minioUtil;

    @GetMapping("/list")
    public CommonResponse<?> list(PictureRequest pictureRequest) {
        List<PictureInfo> pictures = pictureMapper.selectList(Wrappers.<PictureInfo>lambdaQuery()
                .eq(StringUtils.isNotBlank(pictureRequest.getImageName()), PictureInfo::getFileName, pictureRequest.getImageName())
                .eq(StringUtils.isNotBlank(pictureRequest.getImageType()), PictureInfo::getFileType, pictureRequest.getImageType()));

        List<PictureVO> result = pictures.stream().map(picture -> {
            PictureVO pictureVO = new PictureVO();
            BeanUtil.copyProperties(picture, pictureVO);
            return pictureVO;
        }).toList();

        // 每次查询从minio重新获取访问链接
        result.forEach(pictureVO -> {
            pictureVO.setImageId(String.valueOf(pictureVO.getId()));
            pictureVO.setFilePath(minioUtil.getFileUrl(pictureVO.getStoreName()));
        });
        return CommonResponse.success(result);
    }

    @GetMapping("/{id}")
    public CommonResponse<?> getImageInfo(@PathVariable Long id) {
        List<PictureInfo> pictures = pictureMapper.selectList(Wrappers.<PictureInfo>lambdaQuery()
                .eq(PictureInfo::getId, id)
                .eq(PictureInfo::getStatus, 0)
                .eq(PictureInfo::getIsDeleted, 0));

        List<PictureVO> result = pictures.stream().map(picture -> {
            PictureVO pictureVO = new PictureVO();
            BeanUtil.copyProperties(picture, pictureVO);
            return pictureVO;
        }).toList();

        // 每次查询从minio重新获取访问链接
        result.forEach(pictureVO -> {
            pictureVO.setImageId(String.valueOf(pictureVO.getId()));
            pictureVO.setFilePath(minioUtil.getFileUrl(pictureVO.getStoreName()));
        });
        return CommonResponse.success(result);
    }

    @PostMapping("/upload")
    public CommonResponse<?> upload(@RequestParam("files") MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return CommonResponse.failed("没有选择任何文件");
        }

        List<String> successFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        List<String> errorDetails = new ArrayList<>();

        List<PictureInfo> pictures = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                // 验证文件类型
                if (!isValidFileType(file.getOriginalFilename())) {
                    failedFiles.add(file.getOriginalFilename());
                    errorDetails.add(file.getOriginalFilename() + " - 文件类型不支持");
                    continue;
                }

                // 验证文件大小
                if (file.getSize() > MAX_FILE_SIZE) {
                    failedFiles.add(file.getOriginalFilename());
                    errorDetails.add(file.getOriginalFilename() + " - 文件大小超过限制");
                    continue;
                }

                // 生成唯一文件名以避免冲突
                String originalFilename = file.getOriginalFilename();
                String fileExtension = getFileExtension(originalFilename);
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                // 上传文件到MinIO
                minioUtil.upload(file, uniqueFilename);

                System.out.println("上传成功 - 文件名：" + originalFilename + ", 大小：" + file.getSize() + " bytes");

                PictureInfo picture = PictureInfo.builder()
                        .fileName(originalFilename)
                        .filePath(minioUtil.getFileUrl(uniqueFilename))
                        .fileSize(String.valueOf(file.getSize()))
                        .fileType(file.getContentType())
                        .storeName(uniqueFilename)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                successFiles.add(originalFilename);
                pictures.add(picture);

            } catch (Exception e) {
                System.err.println("上传失败 - 文件：" + file.getOriginalFilename() + ", 错误：" + e.getMessage());
                failedFiles.add(file.getOriginalFilename());
                errorDetails.add(file.getOriginalFilename() + " - " + e.getMessage());
            }
        }

        List<BatchResult> imageUploadResult = pictureMapper.insert(pictures);

        if (imageUploadResult.isEmpty()) {
            log.info("所有图片信息插入数据库成功");
        } else {
            log.info("部分图片信息插入数据库成功");
        }

        // 构建响应结果
        UploadResult result = new UploadResult();
        result.setSuccessCount(successFiles.size());
        result.setSuccessFiles(successFiles);
        result.setFailedCount(failedFiles.size());
        result.setFailedFiles(errorDetails);

        if (failedFiles.isEmpty()) {
            log.info("所有文件上传成功");
            return CommonResponse.success(result, "所有文件上传成功");
        } else if (successFiles.isEmpty()) {
            log.info("所有文件上传失败");
            return CommonResponse.failed("所有文件上传失败");
        } else {
            log.info("部分文件上传成功");
            return CommonResponse.success(result, "部分文件上传成功");
        }
    }

    /**
     * 验证文件类型是否允许
     */
    private boolean isValidFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }

        String extension = getFileExtension(filename).toLowerCase();
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * 上传结果数据类
     */
    public static class UploadResult {
        private int successCount;
        private List<String> successFiles;
        private int failedCount;
        private List<String> failedFiles;

        // Getters and Setters
        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public List<String> getSuccessFiles() {
            return successFiles;
        }

        public void setSuccessFiles(List<String> successFiles) {
            this.successFiles = successFiles;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public void setFailedCount(int failedCount) {
            this.failedCount = failedCount;
        }

        public List<String> getFailedFiles() {
            return failedFiles;
        }

        public void setFailedFiles(List<String> failedFiles) {
            this.failedFiles = failedFiles;
        }
    }
}