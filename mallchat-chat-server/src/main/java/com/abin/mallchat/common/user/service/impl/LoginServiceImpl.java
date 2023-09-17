package com.abin.mallchat.common.user.service.impl;

import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.utils.JwtUtils;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.common.user.service.LoginService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description
 */
@Service
public class LoginServiceImpl implements LoginService {

    /**
     * access_token的过期时间
     */
    public static final int TOKEN_EXPIRE_DAYS = 3;

    /**
     * refresh_token的过期时间
     */
    public static final int TOKEN_RENEWAL_DAYS = 1;


    @Resource
    private JwtUtils jwtUtils;

    /**
     * 刷新token
     *
     * @param token 原token
     */
    @Async
    @Override
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expireDays = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if (expireDays == -2) {
            // 不存在的key
            return;
        }
        // 只要token的过期时间小于token刷新时间就进行token刷新
        if (expireDays < TOKEN_RENEWAL_DAYS) {
            RedisUtils.expire(getUserTokenKey(uid), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    /**
     * 登录
     *
     * @param uid 用户ID
     * @return token
     */
    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 从token中解析有效的用户ID
     *
     * @param token token
     * @return 用户ID
     */
    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return null;
        }
        // 通过解析出的用户ID，从redis中获取旧token
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        return Objects.equals(oldToken, token) ? uid : null;
    }

    /**
     * 获取用户存在redis中的token key
     *
     * @param uid 用户ID
     * @return redis中的token key
     */
    private String getUserTokenKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_KEY, uid);
    }
}
