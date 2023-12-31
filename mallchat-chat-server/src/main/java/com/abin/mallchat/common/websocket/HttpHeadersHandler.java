package com.abin.mallchat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description
 */
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {

    /**
     * 请求头中真实IP的key
     */
    public static final String X_REAL_IP = "X-Real-IP";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 过滤Websocket请求（在升级成Websocket请求前进行请求头信息的截取）
        if (msg instanceof HttpRequest) {
            // 强转HttpRequest对象
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.getUri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            // 如果token存在，在上下问对象中插入token属性，以便后续使用
            tokenOptional.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            // 移除后面拼接的所有参数，不然后面进行解析的时候，会把token参数解析成url，从而导致连接失败
            request.setUri(urlBuilder.getPath().toString());
            // 获取用户ip（注意：这里如果使用了负载均衡服务器（Nginx等），一定要把真实的IP地址设置回请求头中，不然获取到的就是负载均衡服务器的IP地址）
            String ip = request.headers().get(X_REAL_IP);
            if (StringUtils.isBlank(ip)) {
                // 如果为空，则获取当前连接的远端地址
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            // 保存到上下文属性中
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            // 处理器只需要用一次，用完就进行移除
            ctx.pipeline().remove(this);
        }
        ctx.fireChannelRead(msg);
    }
}
