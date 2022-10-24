package com.wzy.kts.controller;

import com.wzy.kts.entity.Response;
import com.wzy.kts.entity.user.FriendInfo;
import com.wzy.kts.entity.user.UserInfo;
import com.wzy.kts.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/friend")
@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping("/addfriend")
    public Response<String> addFriend(@RequestBody FriendInfo friendInfo) {
        return friendService.saveFriend(friendInfo);
    }

    @PostMapping("/getfriends")
    public Response<List<UserInfo>> getFriends(String id) {
        return friendService.getFriendInfo(id);
    }
}
