package com.wzy.kts.entity;

/**
 * @author yu.wu
 * @description 消息内容类型枚举
 * @date 2022/10/20 21:27
 */
public enum MsgType {
    TEXT("TEXT","文本"),

    IMAGE("IMAGE", "图片"),

    FILE("FILE", "文件"),

    ERROR_TYPE("ERROR_TYPE", "错误类型");

    private String type;
    private String desc;

    MsgType(String type,String desc){
        this.type = type;
        this.desc = desc;
    }

    public String getType(){
        return type;
    }

    public static MsgType getByType(String type){
        for (MsgType value : values()) {
            if (value.getType().equalsIgnoreCase(type)){
                return value;
            }
        }
        return ERROR_TYPE;
    }
}
