package com.abin.mallchat.common.websocket;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description
 */
public class NettyUtil {

    /**
     * 存于上下文对象中的token key
     */
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    /**
     * 存于上下文对象中的ip key
     */
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");

    /**
     * 向websocket上下文对象中设置属性
     * 类似于向HttpRequest对象中设置属性
     *
     * @param channel 当前频道
     * @param key     设置的Key
     * @param value   设置值
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> key, T value) {
        Attribute<T> attr = channel.attr(key);
        attr.set(value);
    }

    /**
     * 获取websocket上下文对象中的属性
     *
     * @param channel 当前频道
     * @param key     需要获取的Key
     */
    public static <T> T getAttr(Channel channel, AttributeKey<T> key) {
        Attribute<T> attr = channel.attr(key);
        return attr.get();
    }
}
