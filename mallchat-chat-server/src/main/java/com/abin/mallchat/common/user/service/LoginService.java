package com.abin.mallchat.common.user.service;

public interface LoginService {
    /**
     * 刷新token有效期
     *
     * @param token token
     */
    void renewalTokenIfNecessary(String token);

    /**
     * 登录成功，获取token
     *
     * @param uid 用户ID
     * @return 返回token
     */
    String login(Long uid);

    /**
     * 如果token有效，返回uid
     *
     * @param token token
     * @return 用户ID
     */
    Long getValidUid(String token);
}
