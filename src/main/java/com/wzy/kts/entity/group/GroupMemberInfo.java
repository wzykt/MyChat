package com.wzy.kts.entity.group;

import lombok.Data;

/**
 * @author yu.wu
 * @description 群内成员信息
 * @date 2022/10/23 17:06
 */
@Data
public class GroupMemberInfo {
    /** 群组Id*/
    private String groupId;

    private String userId;

    private String userName;

    private String avatar;
}
