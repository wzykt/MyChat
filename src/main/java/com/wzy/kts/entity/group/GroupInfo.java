package com.wzy.kts.entity.group;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wzy.kts.entity.BaseEntity;
import lombok.Data;

/**
 * @author yu.wu
 * @description 群组信息
 * @date 2022/10/23 18:01
 */
@Data
@TableName("mychat_group_info_l")
public class GroupInfo extends BaseEntity {

    private String groupId;

    private String groupName;

    /**
     * 群主
     */
    private String groupOwner;

    /**
     * 群聊创建人,跟群主概念不同,群主可变,创建人不可变,最开始群主是创建人
     */
    private String groupCreator;

}