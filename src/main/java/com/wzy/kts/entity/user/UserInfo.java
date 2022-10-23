package com.wzy.kts.entity.user;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wzy.kts.entity.BaseEntity;
import lombok.Data;

/**
 * @author yu.wu
 * @description 用户信息实体
 * @date 2022/10/23 18:11
 */
@Data
@TableName("mychat_user_info_l")
public class UserInfo extends BaseEntity {

    private String userId;

    private String userName;

    private String password;

    private String avatar;

    private String description;

    private String background;

    @TableField(exist = false)
    private Integer days;
}