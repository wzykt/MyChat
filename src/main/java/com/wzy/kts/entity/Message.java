package com.wzy.kts.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author yu.wu
 * @description 消息体
 * @date 2022/10/19 23:53
 */
@Data
@TableName("mychat_message_l")
public class Message {
    /*
    消息序列号：时间戳
     */
    private long msgSeq;

    /*
    发送者
     */
    private String from;

    /*
    接收者
     */
    private String to;


    /*
    消息内容
     */
    private String message;

    /*
    消息内容类型 文字、文件、图片
     */
    private String msgType;

    /*
    消息类型
     */
    private String type = Type.SINGLE.getType();
}
