package com.wzy.kts.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzy.kts.dao.GroupInfoMapper;
import com.wzy.kts.dao.GroupMemberInfoMapper;
import com.wzy.kts.dao.GroupMemberMapper;
import com.wzy.kts.entity.Response;
import com.wzy.kts.entity.ResponseCode;
import com.wzy.kts.entity.dto.GroupDTO;
import com.wzy.kts.entity.dto.GroupInfoDTO;
import com.wzy.kts.entity.group.GroupInfo;
import com.wzy.kts.entity.group.GroupMember;
import com.wzy.kts.entity.group.GroupMemberInfo;
import com.wzy.kts.util.UserIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yu.wu
 * @description
 * @date 2022/10/23 16:54
 */
@Service
@Slf4j
public class GroupService {

    private final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private UserIdGenerate userIdGenerate;

    @Autowired
    private GroupMemberInfoMapper groupMemberInfoMapper;

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    /**
     * @param groupDTO
     * @return
     * @description 创建群组，群组信息存入数据库
     */
    public Response<GroupInfoDTO> createGroup(GroupDTO groupDTO) {
        if (groupDTO == null) {
            return Response.error(ResponseCode.PARAM_EMPTY);
        }
        GroupInfo groupInfo = createGroupInfo(groupDTO);
        savaGroupMember(groupInfo.getGroupId(), groupDTO.getGroupMembers());
        //todo 直接返回群聊信息和群成员详细信息
        return Response.success(createGroupInfoDTO(groupInfo.getGroupId()));
    }

    /**
     * @param groupDTO
     * @return
     * @description 生成群组信息
     */
    private GroupInfo createGroupInfo(GroupDTO groupDTO) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupId(userIdGenerate.generateGroupId());
        groupInfo.setGroupName(groupDTO.getGroupName());
        groupInfo.setGroupOwner(groupDTO.getGroupMembers().get(0));
        groupInfo.setGroupCreator(groupDTO.getGroupMembers().get(0));
        return groupInfo;
    }

    /**
     * @param groupId
     * @param groupMembers
     * @description 保存群组Id和群成员的映射关系
     */
    private void savaGroupMember(String groupId, List<String> groupMembers) {
        for (String groupMemberId : groupMembers) {
            groupMemberMapper.insert(new GroupMember(groupId, groupMemberId));
        }
    }

    private GroupInfoDTO createGroupInfoDTO(String groupId) {
        List<GroupMemberInfo> groupMemberInfos = groupMemberInfoMapper.findMembersByGroupId(groupId);
        QueryWrapper<GroupInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        GroupInfo groupInfo = groupInfoMapper.selectOne(queryWrapper);
        return new GroupInfoDTO(groupInfo.getGroupId(), groupInfo.getGroupName(), groupInfo.getGroupCreator()
                , groupInfo.getGroupOwner(), groupMemberInfos);
    }

    /**
     * @param groupId
     * @description 根据groupId返回群成员信息
     */
    public Response<List<GroupMemberInfo>> members(String groupId) {
        List<GroupMemberInfo> groupMemberInfos = groupMemberInfoMapper.findMembersByGroupId(groupId);
        return Response.success(groupMemberInfos);
    }

    /**
     * @param userId
     * @return
     * @description 返回该userId的所有群聊信息
     */
    public Response<List<GroupInfoDTO>> groupInfoByUserId(String userId) {
        List<String> groupIds = groupMemberMapper.findGroupIdByUserId(userId);
        List<GroupInfoDTO> list = new ArrayList<>();
        for (String groupId : groupIds) {
            list.add(createGroupInfoDTO(groupId));
        }
        return Response.success(list);
    }
}
