package com.wzy.kts.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author yu.wu
 * @description 实体基类
 * @date 2022/10/23 17:14 
 */
@Data
public class BaseEntity {

    // TODO: 2022/10/23 所有表都有相同的Id列，不太好吧 
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField(fill = FieldFill.INSERT)
    private Long created;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updated;
    
}
