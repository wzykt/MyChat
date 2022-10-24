package com.wzy.kts.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzy.kts.dao.FriendMapper;
import com.wzy.kts.dao.UserMapper;
import com.wzy.kts.entity.Response;
import com.wzy.kts.entity.user.FriendInfo;
import com.wzy.kts.entity.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Slf4j
public class FriendService {

    Logger logger = LoggerFactory.getLogger(FriendService.class);

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserMapper userMapper;

    public Response<String> saveFriend(FriendInfo friendInfo){
        friendMapper.insert(friendInfo);
        return Response.success();
    }

    /**
     * @description 获取好友信息
     * @param userId
     * @return
     */
    public Response<List<UserInfo>> getFriendInfo(String userId) {
        final List<String> userIds = friendMapper.findFriendByUserId(userId);
        if (ObjectUtils.isEmpty(userId)) {
            return Response.success(null);
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(userId, userIds);
        final List<UserInfo> userInfos = userMapper.selectList(queryWrapper);
        return Response.success(userInfos);
    }


}
