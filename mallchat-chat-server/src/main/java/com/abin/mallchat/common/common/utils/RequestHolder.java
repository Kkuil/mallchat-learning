package com.abin.mallchat.common.common.utils;

import com.abin.mallchat.common.common.domain.dto.RequestInfo;

/**
 * @Author Kkuil
 * @Date 2023/9/18
 * @Description 请求上下文
 */
public class RequestHolder {
    private static final ThreadLocal<RequestInfo> THREAD_LOCAL = new ThreadLocal<RequestInfo>();

    public static void set(RequestInfo requestInfo) {
        THREAD_LOCAL.set(requestInfo);
    }

    public static RequestInfo get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
