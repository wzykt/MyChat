package com.wzy.kts.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.kts.entity.group.GroupMemberInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMemberInfoMapper extends BaseMapper<GroupMemberInfo> {

    List<GroupMemberInfo> findMembersByGroupId(String groupId);

}
