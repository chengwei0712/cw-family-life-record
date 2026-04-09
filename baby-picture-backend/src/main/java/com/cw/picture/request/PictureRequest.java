package com.cw.picture.request;

import lombok.Data;

import java.util.Date;

/**
 * 图片请求类
 *
 * @author chengwei
 * @since 2025.12.30
 */
@Data
public class PictureRequest {

    private String imageName;

    private String imagePath;

    private String imageSize;

    private String imageType;

    private Date startTime;

    private Date endTime;
}
