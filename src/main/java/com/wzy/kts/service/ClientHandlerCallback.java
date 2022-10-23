package com.wzy.kts.service;

import com.wzy.kts.handler.ClientHandler;

/**
 * @author yu.wu
 * @description 客户端处理回调接口
 * @date 2022/10/19 23:30
 */
public interface ClientHandlerCallback {

    /**
     * @description 通知服务器断开当前客户端的链接
     * @param clientHandler
     */
    void closeClient(ClientHandler clientHandler);

    /**
     * @description 通知服务端接收消息
     * @param clientHandler
     * @param message
     */
    void receiveMessage(ClientHandler clientHandler,String message);
}
