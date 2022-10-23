package com.wzy.kts.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 林北
 * Description 创建群聊DTO
 * @date 2021-12-15 22:57
 */
@Data
public class GroupDTO {
    /** 群聊名 */
    private String groupName;
    /** 群聊成员 封装时，群主做为第0个元素传入*/
    private List<String> groupMembers;
}
