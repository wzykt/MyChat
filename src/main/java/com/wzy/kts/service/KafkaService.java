package com.wzy.kts.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzy.kts.dao.MessageMapper;
import com.wzy.kts.dao.UserGroupMessageMapper;
import com.wzy.kts.entity.Message;
import com.wzy.kts.entity.Response;
import com.wzy.kts.entity.group.UserGroupMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yu.wu
 * @description
 * @date 2022/10/22 22:48
 */
@Slf4j
@Service
public class KafkaService {

    @Autowired
    private MessageMapper messageMapper;

    private UserGroupMessageMapper userGroupMessageMapper;

    /**
     * 单聊消息入库
     * @param record
     */
    @KafkaListener(topics = {"mychat"})
    public void receive(ConsumerRecord<String,String> record){
        log.info("receive: topic:{}, partition:{}, value:{},offset:{}", record.topic(), record.partition(), record.value(),
                record.offset());
        Message message = JSONUtil.toBean(record.value(), Message.class);
        if (message != null) {
            log.info("消费Kafka: {}", message);
            messageMapper.insert(message);
        }
    }

    /**
     * 群聊消息映射对象入库
     * @param record
     */
    @KafkaListener(topics = {"mychat-group"})
    public void receiveGroup(ConsumerRecord<String, String> record) {
        log.info("receive: topic:{}, partition:{}, value:{},offset:{}", record.topic(), record.partition(), record.value(),
                record.offset());
        UserGroupMessage userGroupMessage = JSONUtil.toBean(record.value(), UserGroupMessage.class);
        if (userGroupMessage != null) {
            log.info("消费Kafka: {}", userGroupMessage);
            userGroupMessageMapper.insert(userGroupMessage);
        }
    }

    // TODO: 2022/10/22 不懂这边的 getOffLine 操作
    public Response<List<Message>> getOffLine(String id) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receive_id", id);
        final List<Message> messageList = messageMapper.getOffLineMessage(id);
        List<Message> groupMessage = getOffLineGroupMessage(id);
        if (messageList.size() > 0) {
            messageList.addAll(groupMessage);
            messageMapper.delete(queryWrapper);
        }
        return messageList.size() > 0 ? Response.success(messageList) : Response.success(groupMessage);
    }

    public List<Message> getOffLineGroupMessage(String userId) {
        QueryWrapper<UserGroupMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<UserGroupMessage> userGroupMessageList = userGroupMessageMapper.selectList(queryWrapper);
        if (userGroupMessageList.size() > 0) {
            userGroupMessageMapper.delete(queryWrapper);
        }
        List<String> groupIds = userGroupMessageList.stream().map(UserGroupMessage::getGroupId).collect(Collectors.toList());
        List<Long> msgSeqs = userGroupMessageList.stream().map(UserGroupMessage::getMsgSeq).collect(Collectors.toList());
        if (groupIds.size() > 0) {
            List<Message> groupMsgByGroupId = messageMapper.getGroupMsgByGroupId(groupIds);
            List<Message> groupMessage = groupMsgByGroupId.stream().filter(msg -> msgSeqs.contains(msg.getMsgSeq())).collect(Collectors.toList());
            return groupMessage;
        }
        return Collections.emptyList();
    }
}
