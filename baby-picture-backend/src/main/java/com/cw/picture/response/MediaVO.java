package com.cw.picture.response;

import lombok.Data;

/**
 * 媒体VO
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Data
public class MediaVO {
    
    /**
     * 媒体ID
     */
    private Long id;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件路径/访问URL
     */
    private String filePath;
    
    /**
     * 文件大小
     */
    private String fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 媒体类型：photo/video
     */
    private String mediaType;
}