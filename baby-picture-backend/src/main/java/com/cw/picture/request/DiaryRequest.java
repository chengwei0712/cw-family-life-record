package com.cw.picture.request;

import lombok.Data;

/**
 * 日记请求类
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Data
public class DiaryRequest {

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
     * 媒体 ID 列表
     */
    private Long[] mediaIds;
}
