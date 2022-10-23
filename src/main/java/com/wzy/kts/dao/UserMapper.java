package com.wzy.kts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.kts.entity.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {
}
