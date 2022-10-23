package com.wzy.kts.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzy.kts.dao.UserMapper;
import com.wzy.kts.entity.Response;
import com.wzy.kts.entity.ResponseCode;
import com.wzy.kts.entity.user.UserInfo;
import com.wzy.kts.util.UserIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author yu.wu
 * @description
 * @date 2022/10/23 18:08
 */
@Service
@Slf4j
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${endpoint}")
    private String endPoint;

    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${bucket}")
    private String bucket;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserIdGenerate userIdGenerate;

    public Response<UserInfo> saveUserInfo(UserInfo userInfo) {
        if (StringUtils.hasText(userInfo.getUserName()) || !StringUtils.hasText(userInfo.getPassword())) {
            return Response.error(ResponseCode.PARAM_EMPTY, "用户名或者密码不能为空");
        }
        if (!StringUtils.hasText(userInfo.getAvatar())) {
            // TODO: 2022/10/23  设置默认头像，暂时还没有
            userInfo.setAvatar("");
        }
        if (!StringUtils.hasText(userInfo.getBackground())) {
            // TODO: 2022/10/23  设置默认背景图,暂时还没有
            userInfo.setBackground("");
        }
        if (StringUtils.hasText(userInfo.getUserId())) {
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userInfo.getUserId());
            final UserInfo old = userMapper.selectOne(queryWrapper);
            if (old == null) {
                return Response.error(ResponseCode.PARAM_FAIL, "参数校验失败");
            }
        }
        if (!StringUtils.hasText(userInfo.getUserId())) {
            userInfo.setUserId(userIdGenerate.generateUserId());
        }
        userMapper.insert(userInfo);
        return Response.success();
    }

    public Response<UserInfo> login(UserInfo userInfo) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userInfo.getUserName());
        final UserInfo one = userMapper.selectOne(queryWrapper);
        // TODO: 2022/10/23 密码未校验
        if (one == null || !one.getPassword().equals(userInfo.getPassword())) {
            return Response.error(ResponseCode.PARAM_FAIL, "用户名或者密码错误");
        }
        logger.info(one.toString());
        calDays(one);
        setCache(one);
        return Response.success(one);
    }

    private void calDays(UserInfo userInfo) {
        long betweenDay = DateUtil.between(new Date(userInfo.getCreated()), new Date(), DateUnit.DAY);
        userInfo.setDays(betweenDay == 0 ? 1 : (int) betweenDay);
    }

    /**
     * @param userInfo
     * @description 把登录的用户ID存入redis
     */
    private void setCache(UserInfo userInfo) {
        stringRedisTemplate.opsForValue().set(userInfo.getUserId(), "1");
    }

    public Response<List<UserInfo>> search(String key) {
        if (!StringUtils.hasText(key)) {
            return Response.error(ResponseCode.PARAM_EMPTY, "关键字不能为空");
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("user_name", key);
        List<UserInfo> userInfos = userMapper.selectList(queryWrapper);
        return Response.success(userInfos);
    }

    public Response<List<UserInfo>> getFriendInfo(String userId) {

        return Response.success();
    }

    public Response<UserInfo> uploadFile(MultipartFile file, String userId, boolean flag) {
        String url = handlerFile(file, flag);
        if (!StringUtils.hasText(url)) {
            return Response.error(ResponseCode.ERROR, flag ? "背景图" : "头像" + "上传失败");
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        UserInfo userInfo = userMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(userInfo)) {
            return Response.error(ResponseCode.PARAM_FAIL, "该用户不存在");
        }
        if (flag) {
            userInfo.setBackground(url);
        } else {
            userInfo.setAvatar(url);
        }
        return saveUserInfo(userInfo);
    }

    private String handlerFile(MultipartFile file, boolean flag) {
        String url = "";
        if(file != null){
            String originalFilename = "";
            if(file.getOriginalFilename() != null && !"".equals(originalFilename = file.getOriginalFilename())){
                File localFile = new File(originalFilename);
                try (FileOutputStream outputStream = new FileOutputStream(localFile)) {
                    outputStream.write(file.getBytes());
                    file.transferTo(localFile);
                    url = uploadLocalFileToOSS(localFile, flag);
                } catch (IOException e) {
                    log.error("handlerFileList cause {}", e.getMessage());
                } finally {
                    if (!localFile.delete()) {
                        log.error("本地文件删除失败: {}", localFile.getName());
                    }
                }
            }
        }
        return url;
    }

    /**
     * @description 上传到阿里云OSS
     * @param localFile
     * @param flag
     * @return
     */
    private String uploadLocalFileToOSS(File localFile, boolean flag) {
        return "";
    }
}
