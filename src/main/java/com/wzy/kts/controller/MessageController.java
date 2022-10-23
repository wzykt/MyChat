package com.wzy.kts.controller;

import com.wzy.kts.entity.Message;
import com.wzy.kts.entity.Response;
import com.wzy.kts.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * @author yu.wu
 * @description
 * @date 2022/10/22 22:48
 */
@RestController
public class MessageController {

    @Autowired
    private KafkaService kafkaService;

    @GetMapping
    public Response<List<Message>> offLineMessage(@PathVariable("id") String id) {
        return kafkaService.getOffLine(id);
    }

}
