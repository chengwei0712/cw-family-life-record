package com.cw.picture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cw.picture.entity.PictureInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图片信息Mapper
 *
 * @author chengwei
 * @since 2025-12-30 16:41:00
 */
@Mapper
public interface PictureMapper extends BaseMapper<PictureInfo> {
}
