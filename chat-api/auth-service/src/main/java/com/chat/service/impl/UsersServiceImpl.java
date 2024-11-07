package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chat.enums.Sex;
import com.chat.utils.LocalDateUtils;
import jakarta.annotation.Resource;
import com.chat.base.BaseInfoProperties;
import com.chat.mapper.UsersMapper;
import com.chat.pojo.Users;
import com.chat.service.UsersService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户表 服务实现类
 */
@Service
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {

    private static String IMAGE = "C:/Users/hp/Desktop/头像.jpg";

    @Resource
    private UsersMapper usersMapper;

    @Override
    public Users queryMobileIfExist(String mobile) {
        return usersMapper.selectOne(new QueryWrapper<Users>().eq("mobile", mobile));
    }

    @Override
    public Users createUsers(String mobile, String nickName) {
        Users users = new Users();
        users.setMobile(mobile);
        // 随机生成字符串
        String[] uuidStr = UUID.randomUUID().toString().split("-");
        String wechatNum = "wx" + uuidStr[0] + uuidStr[1];
        // 微信号
        users.setWechatNum(wechatNum);
        // 微信二维码
        users.setWechatNumImg(IMAGE);
        // 昵称
        users.setNickname(StringUtils.isBlank(nickName) ? "新用户" + UUID.randomUUID().toString().replace("-", "").substring(0, 5) : nickName);
        // 性别
        users.setSex(Sex.secret.type);
        // 头像
        users.setFace(IMAGE);
        // 朋友圈默认背景图
        users.setFriendCircleBg(IMAGE);
        // 邮箱
        users.setEmail("");
        // 生日
        users.setBirthday(LocalDateUtils.parseLocalDate("2000-01-01", LocalDateUtils.DATE_PATTERN));
        // 地区
        users.setCountry("中国");
        // 省份
        users.setProvince("");
        // 城市
        users.setCity("");
        // 区县
        users.setDistrict("");
        // 创建时间
        users.setCreatedTime(LocalDateTime.now());
        // 编辑时间
        users.setUpdatedTime(LocalDateTime.now());
        // 将用户数据保存到数据库中
        usersMapper.insert(users);
        // 返回
        return users;
    }
}