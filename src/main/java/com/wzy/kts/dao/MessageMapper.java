package com.wzy.kts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.kts.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author yu.wu
 * @description
 * @date 2022/10/22 22:04
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    List<Message> getOffLineMessage(String receiveId);

    void insertGroupMessage(Message message);

    List<Message> getGroupMsgByGroupId(List<String> list);

}
