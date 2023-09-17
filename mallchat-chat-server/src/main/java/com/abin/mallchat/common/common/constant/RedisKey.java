package com.abin.mallchat.common.common.constant;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 
 */
public class RedisKey {
    private static final String BASE_KEY = "mallchat:chat";
    /**
     * 用户token的key
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    public static String getKey(String key, Object... o) {
        return BASE_KEY + String.format(key, o);
    }
}
