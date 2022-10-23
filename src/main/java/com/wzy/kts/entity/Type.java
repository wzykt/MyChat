package com.wzy.kts.entity;

/**
 * @author yu.wu
 * @description 消息类型
 * @date 2022/10/20 0:00
 */
public enum Type {

    /** 初始化 */
    INIT("INIT"),

    QUIT("QUIT"),

    /**
     * 单聊
     */
    SINGLE("SINGLE"),

    /**
     * 群聊
     */
    GROUP("GROUP"),

    ERROR_TYPE("ERROR_TYPE");

    private String desc;

    Type(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return desc;
    }

    public static Type getByType(String type) {
        for (Type value : values()) {
            if (value.getType().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return ERROR_TYPE;
    }
}
