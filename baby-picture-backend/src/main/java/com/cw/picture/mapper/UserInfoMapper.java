package com.cw.picture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cw.picture.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息Mapper
 *
 * @author chengwei
 * @since 2025-12-30
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}