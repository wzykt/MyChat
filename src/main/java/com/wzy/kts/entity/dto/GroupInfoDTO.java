package com.wzy.kts.entity.dto;

import com.wzy.kts.entity.group.GroupMemberInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yu.wu
 * @description 群聊详细信息DTO
 * @date 2022/10/23 17:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfoDTO {

    /**
     * 群组创建后生成的groupId
     */
    private String groupId;
    /**
     * 群组名称
     */
    private String groupName;
    /**
     * 群组创建人
     */
    private String groupCreator;
    /**
     * 群主
     */
    private String groupOwner;
    /**
     * 群组成员
     */
    private List<GroupMemberInfo> groupMembers;
}
