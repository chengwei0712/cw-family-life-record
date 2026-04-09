package com.cw.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cw.picture.entity.DiaryInfo;
import com.cw.picture.entity.DiaryMedia;
import com.cw.picture.mapper.DiaryMapper;
import com.cw.picture.mapper.DiaryMediaMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日记服务
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Slf4j
@Service
public class DiaryService {

    @Resource
    private DiaryMapper diaryMapper;

    @Resource
    private DiaryMediaMapper diaryMediaMapper;

    /**
     * 创建日记
     */
    @Transactional
    public DiaryInfo createDiary(Long userId, String title, String content, String mood, String weather, List<Long> mediaIds) {
        DiaryInfo diary = DiaryInfo.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .mood(mood)
                .weather(weather)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(0)
                .build();
        
        diaryMapper.insert(diary);
        
        // 关联媒体文件
        if (mediaIds != null && !mediaIds.isEmpty()) {
            for (int i = 0; i < mediaIds.size(); i++) {
                DiaryMedia dm = DiaryMedia.builder()
                        .diaryId(diary.getId())
                        .mediaId(mediaIds.get(i))
                        .sortOrder(i)
                        .build();
                diaryMediaMapper.insert(dm);
            }
        }
        
        log.info("创建日记成功: id={}, title={}, 媒体数量={}", diary.getId(), title, mediaIds != null ? mediaIds.size() : 0);
        return diary;
    }

    /**
     * 分页查询日记
     */
    public Page<DiaryInfo> listDiaries(Long userId, int page, int size, String keyword) {
        Page<DiaryInfo> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<DiaryInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiaryInfo::getIsDeleted, 0);
        
        if (userId != null) {
            wrapper.eq(DiaryInfo::getUserId, userId);
        }
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(DiaryInfo::getTitle, keyword)
                    .or()
                    .like(DiaryInfo::getContent, keyword));
        }
        
        wrapper.orderByDesc(DiaryInfo::getCreateTime);
        
        return diaryMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取日记详情
     */
    public DiaryInfo getDiaryById(Long id) {
        DiaryInfo diary = diaryMapper.selectById(id);
        if (diary != null && diary.getIsDeleted() == 1) {
            return null;
        }
        return diary;
    }

    /**
     * 获取日记关联的媒体ID列表
     */
    public List<Long> getDiaryMediaIds(Long diaryId) {
        LambdaQueryWrapper<DiaryMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiaryMedia::getDiaryId, diaryId);
        wrapper.orderByAsc(DiaryMedia::getSortOrder);
        
        List<DiaryMedia> list = diaryMediaMapper.selectList(wrapper);
        return list.stream().map(DiaryMedia::getMediaId).toList();
    }

    /**
     * 更新日记
     */
    @Transactional
    public void updateDiary(Long id, String title, String content, String mood, String weather, List<Long> mediaIds) {
        DiaryInfo diary = diaryMapper.selectById(id);
        if (diary == null) {
            throw new RuntimeException("日记不存在");
        }
        
        if (title != null) diary.setTitle(title);
        if (content != null) diary.setContent(content);
        if (mood != null) diary.setMood(mood);
        if (weather != null) diary.setWeather(weather);
        diary.setUpdateTime(LocalDateTime.now());
        
        diaryMapper.updateById(diary);
        
        // 更新媒体关联
        if (mediaIds != null) {
            // 删除旧的关联
            LambdaQueryWrapper<DiaryMedia> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DiaryMedia::getDiaryId, id);
            diaryMediaMapper.delete(wrapper);
            
            // 添加新的关联
            for (int i = 0; i < mediaIds.size(); i++) {
                DiaryMedia dm = DiaryMedia.builder()
                        .diaryId(id)
                        .mediaId(mediaIds.get(i))
                        .sortOrder(i)
                        .build();
                diaryMediaMapper.insert(dm);
            }
        }
        
        log.info("更新日记成功: id={}", id);
    }

    /**
     * 删除日记（软删除）
     */
    public void deleteDiary(Long id) {
        DiaryInfo diary = diaryMapper.selectById(id);
        if (diary != null) {
            diary.setIsDeleted(1);
            diary.setUpdateTime(LocalDateTime.now());
            diaryMapper.updateById(diary);
            log.info("删除日记成功: id={}", id);
        }
    }

    /**
     * 统计日记数量
     */
    public long countDiaries(Long userId) {
        LambdaQueryWrapper<DiaryInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiaryInfo::getIsDeleted, 0);
        if (userId != null) {
            wrapper.eq(DiaryInfo::getUserId, userId);
        }
        return diaryMapper.selectCount(wrapper);
    }
}