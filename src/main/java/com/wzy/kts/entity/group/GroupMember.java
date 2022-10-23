package com.wzy.kts.entity.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wzy.kts.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author yu.wu
 * @description 群组和群成员映射表
 * @date 2022/10/23 17:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mychat_group_member")
public class GroupMember extends BaseEntity {

    /** 群组ID */
    private String groupId;

    /** 群成员id */
    private String groupMemberId;
}
