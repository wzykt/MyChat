package com.wzy.kts.controller;

import com.wzy.kts.entity.Response;
import com.wzy.kts.service.server.ChatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

    @Autowired
    private ChatServer chatServer;

    /**
     * 获取所有用户
     * @return
     */
    @GetMapping("/getClient")
    public Response getClient(){
        return chatServer.getClient();
    }
}
