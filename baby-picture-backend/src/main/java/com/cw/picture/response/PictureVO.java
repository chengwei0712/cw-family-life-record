package com.cw.picture.response;

import com.cw.picture.entity.PictureInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片VO
 *
 * @author chengwei
 * @since 2025.12.30
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class PictureVO extends PictureInfo {

    private String imageId;


}
