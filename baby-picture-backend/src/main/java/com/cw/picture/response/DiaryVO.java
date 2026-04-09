package com.cw.picture.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日记 VO
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Data
public class DiaryVO {

    /**
     * 日记 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 日记标题
     */
    private String title;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 心情
     */
    private String mood;

    /**
     * 天气
     */
    private String weather;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 关联的媒体列表
     */
    private List<MediaVO> mediaList;
}
