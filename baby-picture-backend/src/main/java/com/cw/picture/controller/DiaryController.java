package com.cw.picture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cw.picture.entity.DiaryInfo;
import com.cw.picture.entity.PictureInfo;
import com.cw.picture.mapper.PictureMapper;
import com.cw.picture.response.CommonResponse;
import com.cw.picture.service.DiaryService;
import com.cw.picture.util.MinioUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 日记控制器
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Slf4j
@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Resource
    private DiaryService diaryService;

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private MinioUtil minioUtil;

    /**
     * 创建日记
     */
    @PostMapping("/create")
    public CommonResponse<?> create(@RequestBody DiaryRequest request) {
        try {
            Long userId = 1L;
            
            DiaryInfo diary = diaryService.createDiary(
                    userId,
                    request.getTitle(),
                    request.getContent(),
                    request.getMood(),
                    request.getWeather(),
                    request.getMediaIdsAsLong()
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", String.valueOf(diary.getId()));
            result.put("title", diary.getTitle());
            result.put("createTime", diary.getCreateTime());
            
            return CommonResponse.success(result, "创建成功");
        } catch (Exception e) {
            log.error("创建日记失败", e);
            return CommonResponse.failed("创建失败: " + e.getMessage());
        }
    }

    /**
     * 日记列表
     */
    @GetMapping("/list")
    public CommonResponse<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        
        Long userId = null;
        
        Page<DiaryInfo> result = diaryService.listDiaries(userId, page, size, keyword);
        
        List<Map<String, Object>> list = result.getRecords().stream().map(diary -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", String.valueOf(diary.getId())); // 转为字符串
            item.put("title", diary.getTitle());
            item.put("content", diary.getContent());
            item.put("mood", diary.getMood());
            item.put("weather", diary.getWeather());
            item.put("createTime", diary.getCreateTime());
            
            // 获取关联的媒体详情
            List<Long> mediaIds = diaryService.getDiaryMediaIds(diary.getId());
            List<Map<String, Object>> mediaList = new ArrayList<>();
            for (Long mediaId : mediaIds) {
                PictureInfo pic = pictureMapper.selectById(mediaId);
                if (pic != null && pic.getIsDeleted() == 0) {
                    Map<String, Object> mediaInfo = new HashMap<>();
                    mediaInfo.put("id", String.valueOf(pic.getId())); // 转为字符串
                    mediaInfo.put("fileName", pic.getFileName());
                    mediaInfo.put("fileType", pic.getFileType());
                    mediaInfo.put("fileSize", pic.getFileSize());
                    try {
                        mediaInfo.put("filePath", minioUtil.getFileUrl(pic.getStoreName()));
                    } catch (Exception e) {
                        mediaInfo.put("filePath", "");
                    }
                    mediaList.add(mediaInfo);
                }
            }
            item.put("mediaList", mediaList);
            
            return item;
        }).toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);
        
        return CommonResponse.success(response);
    }

    /**
     * 日记详情
     */
    @GetMapping("/{id}")
    public CommonResponse<?> getDetail(@PathVariable Long id) {
        DiaryInfo diary = diaryService.getDiaryById(id);
        if (diary == null) {
            return CommonResponse.failed("日记不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", String.valueOf(diary.getId())); // 转为字符串
        result.put("title", diary.getTitle());
        result.put("content", diary.getContent());
        result.put("mood", diary.getMood());
        result.put("weather", diary.getWeather());
        result.put("createTime", diary.getCreateTime());
        result.put("updateTime", diary.getUpdateTime());
        
        // 获取关联的媒体详情
        List<Long> mediaIds = diaryService.getDiaryMediaIds(diary.getId());
        List<Map<String, Object>> mediaList = new ArrayList<>();
        for (Long mediaId : mediaIds) {
            PictureInfo pic = pictureMapper.selectById(mediaId);
            if (pic != null && pic.getIsDeleted() == 0) {
                Map<String, Object> mediaInfo = new HashMap<>();
                mediaInfo.put("id", String.valueOf(pic.getId())); // 转为字符串
                mediaInfo.put("fileName", pic.getFileName());
                mediaInfo.put("fileType", pic.getFileType());
                mediaInfo.put("fileSize", pic.getFileSize());
                try {
                    mediaInfo.put("filePath", minioUtil.getFileUrl(pic.getStoreName()));
                } catch (Exception e) {
                    mediaInfo.put("filePath", "");
                }
                mediaList.add(mediaInfo);
            }
        }
        result.put("mediaList", mediaList);
        
        return CommonResponse.success(result);
    }

    /**
     * 更新日记
     */
    @PutMapping("/{id}")
    public CommonResponse<?> update(@PathVariable Long id, @RequestBody DiaryRequest request) {
        try {
            diaryService.updateDiary(
                    id,
                    request.getTitle(),
                    request.getContent(),
                    request.getMood(),
                    request.getWeather(),
                    request.getMediaIdsAsLong()
            );
            return CommonResponse.success("更新成功");
        } catch (Exception e) {
            log.error("更新日记失败", e);
            return CommonResponse.failed("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除日记
     */
    @DeleteMapping("/{id}")
    public CommonResponse<?> delete(@PathVariable Long id) {
        diaryService.deleteDiary(id);
        return CommonResponse.success("删除成功");
    }

    /**
     * 日记统计
     */
    @GetMapping("/stats")
    public CommonResponse<?> stats() {
        long count = diaryService.countDiaries(null);
        return CommonResponse.success(Map.of("diaryCount", count));
    }

    /**
     * 日记请求DTO
     */
    @lombok.Data
    public static class DiaryRequest {
        private String title;
        private String content;
        private String mood;
        private String weather;
        private List<String> mediaIds; // 改为String接收
        
        /**
         * 转换为Long类型的mediaIds
         */
        public List<Long> getMediaIdsAsLong() {
            if (mediaIds == null || mediaIds.isEmpty()) {
                return null;
            }
            return mediaIds.stream()
                    .filter(id -> id != null && !id.isEmpty())
                    .map(Long::parseLong)
                    .toList();
        }
    }
}