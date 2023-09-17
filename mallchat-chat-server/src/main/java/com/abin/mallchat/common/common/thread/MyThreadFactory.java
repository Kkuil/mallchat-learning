package com.abin.mallchat.common.common.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {
    private static final UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = new UncaughtExceptionHandler();
    private ThreadFactory original;

    @Override
    public Thread newThread(Runnable r) {
        // 执行spring线程自己的创建逻辑
        Thread thread = original.newThread(r);
        // 额外装饰我们需要的创建逻辑
        thread.setUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
