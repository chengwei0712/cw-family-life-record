package com.cw.picture.controller;

import com.cw.picture.service.DiaryService;
import com.cw.picture.mapper.PictureMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cw.picture.entity.PictureInfo;
import com.cw.picture.response.CommonResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计控制器
 *
 * @author chengwei
 * @since 2026-04-03
 */
@RestController
@RequestMapping("/stats")
public class StatsController {

    @Resource
    private DiaryService diaryService;

    @Resource
    private PictureMapper pictureMapper;

    /**
     * 获取总统计数据
     */
    @GetMapping("/total")
    public CommonResponse<?> getTotalStats() {
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
        
        // 日记数量
        long diaryCount = diaryService.countDiaries(null);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("photoCount", photoCount);
        stats.put("videoCount", videoCount);
        stats.put("diaryCount", diaryCount);
        stats.put("totalMemories", photoCount + videoCount + diaryCount);
        
        return CommonResponse.success(stats);
    }
}