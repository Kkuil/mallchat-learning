package com.abin.mallchat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 未捕获异常处理器
 */
@Slf4j
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread： {}", e.getMessage());
    }
}
