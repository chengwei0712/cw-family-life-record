package com.cw.picture.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.Date;

/**
 * 图片信息
 *
 * @author chengwei
 * @since 2025.12.30
 */

@TableName("picture_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PictureInfo implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String fileName;

    private String filePath;

    private String fileSize;

    private String fileType;

    private Date createTime;

    private Date updateTime;

    private String storeName;

    private Integer status;

    private Integer isDeleted;
}
