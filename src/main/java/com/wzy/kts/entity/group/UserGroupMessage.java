package com.wzy.kts.entity.group;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author yu.wu
 * @description 群聊信息用户映射
 * @date 2022/10/22 22:14
 */

@Data
@TableName("mychat_user_group_message_l")
public class UserGroupMessage {
    /** 用户ID */
    private String userId;
    /** 群聊ID */
    private String groupId;
    /** 消息ID */
    private Long msgSeq;

    /**
     * Q:为什么要写一个静态方法来new 对象
     * @param userId
     * @param groupId
     * @param msgSeq
     * @return
     */
    public static UserGroupMessage instance(String userId, String groupId, Long msgSeq){
        return new UserGroupMessage(userId, groupId, msgSeq);
    }

    private UserGroupMessage(String userId, String groupId, Long msgSeq) {
        this.userId = userId;
        this.groupId = groupId;
        this.msgSeq = msgSeq;
    }

}
