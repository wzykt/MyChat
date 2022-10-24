package com.wzy.kts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.kts.entity.user.FriendInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<FriendInfo> {

    List<String> findFriendByUserId(String userId);

}
