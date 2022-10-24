package com.wzy.kts.controller;

import com.wzy.kts.entity.Response;
import com.wzy.kts.entity.dto.GroupDTO;
import com.wzy.kts.entity.dto.GroupInfoDTO;
import com.wzy.kts.entity.group.GroupMemberInfo;
import com.wzy.kts.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/save")
    public Response<GroupInfoDTO> createGroup(@RequestBody GroupDTO groupDTO){
        return groupService.createGroup(groupDTO);
    }

    @PostMapping("/members/{groupId}")
    public Response<List<GroupMemberInfo>> members(@PathVariable("groupId") String groupId){
        return groupService.members(groupId);
    }

    @GetMapping("/groupInfo/{userId}")
    public Response<List<GroupInfoDTO>> groupInfoByUserId(@PathVariable("userId") String userId){
        return groupService.groupInfoByUserId(userId);
    }
}
