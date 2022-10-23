package com.wzy.kts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.kts.entity.group.GroupMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author yu.wu
 * @description
 * @date 2022/10/22 22:10
 */
@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMember> {

    /**
     * Description: 根据userId查找出该userId在那些群聊中
     * @param userId
     * @return
     */
    List<String> findGroupIdByUserId(String userId);

    /**
     * 根据groupId查找出成员id
     * @param groupId
     * @return
     */
    List<String> findMemberByGroupId(String groupId);
}
