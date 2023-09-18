package com.abin.mallchat.common.common.interfaces;

/**
 * @Author Kkuil
 * @Date 2023/9/18
 * @Description 在这里为了兼容会抛错的get方法，自定义Supplier
 */
@FunctionalInterface
public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws Throwable 错误
     */
    T get() throws Throwable;
}
