package com.abin.mallchat.common.websocket;

import com.abin.mallchat.common.common.constant.MDCKey;
import com.abin.mallchat.common.common.domain.dto.RequestInfo;
import com.abin.mallchat.common.common.utils.RequestHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @Author Kkuil
 * @Date 2023/09/17 20:30:00
 * @Description Http请求头属性收集器
 */
@Slf4j
public class NettyCollectorHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String tid = UUID.randomUUID().toString();
        MDC.put(MDCKey.TID, tid);
        RequestInfo info = new RequestInfo();
        info.setUid(NettyUtil.getAttr(ctx.channel(), NettyUtil.UID));
        info.setIp(NettyUtil.getAttr(ctx.channel(), NettyUtil.IP));
        RequestHolder.set(info);

        ctx.fireChannelRead(msg);
    }
}
