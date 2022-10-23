package com.wzy.kts.service.server;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzy.kts.dao.GroupMemberMapper;
import com.wzy.kts.dao.MessageMapper;
import com.wzy.kts.entity.Message;
import com.wzy.kts.entity.MsgType;
import com.wzy.kts.entity.Response;
import com.wzy.kts.entity.Type;
import com.wzy.kts.entity.group.UserGroupMessage;
import com.wzy.kts.handler.ClientHandler;
import com.wzy.kts.service.ClientHandlerCallback;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yu.wu
 * @description 服务器Socket
 * @date 2022/10/20 23:26
 */
@Component
@Slf4j
public class ChatServer implements ClientHandlerCallback {

    private final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    protected ConcurrentHashMap<String, Socket> userSocketMap = new ConcurrentHashMap<>();

    //记录所有在线的ClientHandler
    private CopyOnWriteArrayList<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();

    //ClientHandlerId:ClientHandler
    private ConcurrentHashMap<String, ClientHandler> userClientMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    //存放所有在线的ClientHandlerId
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("{chat.server.port}")
    private int port;

    private MessageMapper messageMapper;

    private GroupMemberMapper groupMessageMapper;

    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * @description 启动服务器，socket关联ClientHandler
     */
    private void serverStart() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("******服务器启动*****");
            while (true) {
                logger.info("------正在监听-------");
                Socket client = serverSocket.accept();
                //初始化ClientHandler,并和客户端Socket绑定
                ClientHandler clientHandler = new ClientHandler(client, this);
                //记录所有连接上服务器的客户端
                clientHandlers.add(clientHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param clientHandler
     * @description 客户端下线
     */
    @Override
    public void closeClient(ClientHandler clientHandler) {
        logger.info("客户端：{} 下线了", clientHandler.getClientInfo());
        String key = null;
        for (Map.Entry<String, ClientHandler> next : userClientMap.entrySet()) {
            if (next.getValue().getClientHandlerId().equals(clientHandler.getClientHandlerId())) {
                key = next.getKey();
            }
        }
        userClientMap.remove(key);
        //从redis中移除，表示下线
        stringRedisTemplate.delete(key);
        clientHandlers.remove(clientHandler);
    }

    /**
     * @param clientHandler
     * @param message
     * @description 服务器负责接收并处理消息
     */
    @Override
    public void receiveMessage(ClientHandler clientHandler, String message) {
        //解析读取的消息
        logger.info(clientHandler.getClientInfo() + " : " + message);
        try {
            Message value = objectMapper.readValue(message, Message.class);
            parseType(value, clientHandler);
        } catch (JsonProcessingException e) {
            log.error("receiveMessage JSON 解析失败 : {}", e);
        }
    }

    /**
     * @param message
     * @param clientHandler
     * @description 确定消息如何操作
     */
    public void parseType(Message message, ClientHandler clientHandler) {
        //如果消息类型为空，默认为单聊
        if (message.getType() == null) {
            message.setType(Type.SINGLE.getType());
        }
        switch (Type.getByType(message.getType())) {
            case INIT:
                // TODO: 2022/10/22 是否是我们点击好友头像后，开始建立和好友的链接
                initConnect(message.getFrom(), clientHandler);
                break;
            case SINGLE:
                handlerSingle(message);
                break;
            case GROUP:
                handlerGroup(message);
                break;
            case QUIT:
                // TODO: 2022/10/22 当我们关掉聊天窗口后，会受到该种类型的Message
                handlerQuit(message.getFrom(), clientHandler);
            default:
                throw new UnsupportedOperationException("unsupported type : " + message.getType());
        }
    }

    //
    private void initConnect(String from, ClientHandler clientHandler) {
        if (userClientMap.get(from) == null && isOnline(from)) {
            userClientMap.put(from, clientHandler);
        } else {
            throw new RuntimeException("initConnect 发送异常, " + from + " 重复初始化");
        }
    }

    //单聊
    private <T extends Message> void handlerSingle(T message) {
        switch (MsgType.getByType(message.getMsgType())) {
            case TEXT:
                parseText(message);
                break;
            case IMAGE:
                parseImage(message);
        }
    }

    private void parseText(Message message) {
        String to = message.getTo();
        if (isOnline(to)) {
            ClientHandler clientHandler = getClientHandler(to);
            parseText(message, clientHandler);
        } else {
            sendMsgToKafKa(message);
        }
    }

    private void parseText(Message message, ClientHandler clientHandler) {
        try {
            // TODO: 2022/10/20 完善发送方法
            clientHandler.send(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("parseText JSON 解析失败 : {}", e);
        }
    }

    private void parseImage(Message message) {
        String to = message.getTo();
        if (isOnline(to)) {
            ClientHandler clientHandler = getClientHandler(to);
            parseImage(message, clientHandler);
        }
    }

    private void parseImage(Message message, ClientHandler clientHandler) {
        Message tempMessage = new Message();
        BeanUtils.copyProperties(message, tempMessage);
        handlerUrl(tempMessage);
        try {
            clientHandler.send(objectMapper.writeValueAsString(tempMessage));
        } catch (JsonProcessingException e) {
            logger.error("parseImage JSON 解析失败 : {}", e);
        }
    }

    /**
     * 处理img URL
     * 待处理
     *
     * @param message
     */
    private void handlerUrl(Message message) {
        String prefix = "";
        String suffix = "";
        String imageFlag = message.getMessage();
        log.info("image path : {}", prefix + imageFlag + suffix);
        message.setMessage(prefix + imageFlag + suffix);
    }

    /**
     * 处理群聊消息
     *
     * @param message
     */
    private <T extends Message> void handlerGroup(T message) {
        //获取群Id
        String groupId = message.getTo();
        long msgSeq = message.getMsgSeq();

        //将消息转换成群聊消息
        //1.保持群聊消息
        messageMapper.insertGroupMessage(message);
        //2.保存群成员对群消息的映射关系，在线的直接转发，不在线的存储起来
        //获取群里所有用户Id
        List<String> allMemberList = groupMessageMapper.findMemberByGroupId(groupId);
        //群内在线用户Id
        List<String> onlineMemberList = new ArrayList<>();

        //离线用户 UserGroupMessage:用户和群聊消息的关联
        List<UserGroupMessage> userGroupMessageList = new ArrayList<>();
        logger.info("list: {}", allMemberList.toString());
        for (String userId : allMemberList) {
            //用户在线
            if (isOnline(userId)) {
                //非群聊信息发送方
                if (!userId.equals(message.getFrom())) {
                    onlineMemberList.add(userId);
                }
            } else {
                userGroupMessageList.add(UserGroupMessage.instance(userId, groupId, msgSeq));
            }
        }

        if (!userGroupMessageList.isEmpty()) {
            //sendGroupMsgToKafka(userGroupMessageList);
        }
    }

    private void handlerQuit(String from, ClientHandler clientHandler) {
        if (userClientMap.get(from) != null) {
            ClientHandler mapClientHandler = userClientMap.get(from);
            if (mapClientHandler.getClientHandlerId().equals(clientHandler.getClientHandlerId())) {
                clientHandler.exitBySelf();
                userClientMap.remove(from);
            } else {
                String exception = "handleQuit 发送异常," + from +
                        " 对应的clientHandlerId: " +
                        clientHandler.getClientHandlerId() +
                        " 有误";
                throw new RuntimeException(exception);
            }
        }
    }

    //获取消息发送方的ClientHandler
    private ClientHandler getClientHandler(String to) {
        return userClientMap.get(to);
    }

    /**
     * 判断用户是否在线
     * @param key
     * @return
     */
    public boolean isOnline(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    // TODO: 2022/10/22 感觉没用到
    public Response getClient() {
        return Response.success(userSocketMap.keys());
    }

    /**
     * 使用kafka存储未发送的消息
     * @param data
     */
    private void sendMsgToKafKa(Message data){
        String message = JSONUtil.toJsonStr(data);
        kafkaTemplate.send("mychat", message).addCallback(success -> {
            log.info("成功发送消息到,topic:{} ,partition:{} ,offset:{}", success.getRecordMetadata().topic(),
                    success.getRecordMetadata().partition(), success.getRecordMetadata().offset());
        }, error -> {
            log.error("消息发送失败: {}", error.getMessage());
        });
    }

    private void sendGroupMsgToKafka(List<UserGroupMessage> userGroupMessageList) {
        for (UserGroupMessage userGroupMessage : userGroupMessageList) {
            //离线消息
            String message = JSONUtil.toJsonStr(userGroupMessage);
            // TODO: 2022/10/22 不会kafka的消息推送
            kafkaTemplate.send("mychat-group", message).addCallback(success -> {
                log.info("成功发送消息到,topic:{} ,partition:{} ,offset:{}", success.getRecordMetadata().topic(),
                        success.getRecordMetadata().partition(), success.getRecordMetadata().offset());
            }, error -> {
                log.error("消息发送失败: {}", error.getMessage());
            });
        }
    }
}
