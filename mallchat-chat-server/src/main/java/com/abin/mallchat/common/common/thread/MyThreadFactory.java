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

    /**
     * 开启新线程
     *
     * @param runnable a runnable to be executed by new thread instance
     * @return Thread
     */
    @Override
    public Thread newThread(Runnable runnable) {
        // 执行spring线程自己的创建逻辑
        Thread thread = original.newThread(runnable);
        // 额外装饰我们需要的创建逻辑（相当于代理模式，增强线程的作用）
        thread.setUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
