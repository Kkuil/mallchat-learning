package com.abin.mallchat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.abin.mallchat.common.websocket.domain.vo.req.WSBaseReq;
import com.abin.mallchat.common.websocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description Websocket 处理器
 */
@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    /**
     * 用户上线时，初始化Websocket服务
     *
     * @param ctx 上下文对象
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.online(ctx.channel());
    }

    /**
     * 用户下线时，断开连接
     *
     * @param ctx 上下文对象
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        userOffline(ctx.channel());
    }

    /**
     * 用户触发的行为处理器
     *
     * @param ctx 上下文对象
     * @param evt 触发的事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // 握手认证事件
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 获取在 升级成Websocket协议之前请求头中携带的token值 （在这个类（HttpHeadersHandler）中收集的）
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            // 非空则进行权限验证
            if (StrUtil.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), token);
            }
        }
        // 读空闲事件
        if (evt instanceof IdleStateEvent) {
            // 事件类型强转
            IdleStateEvent event = (IdleStateEvent) evt;
            // 读空闲则触发用户下线事件
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("读空闲");
                userOffline(ctx.channel());
            }
        }
    }

    /**
     * 异常捕获
     *
     * @param ctx   上线对象
     * @param cause 异常原因
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 消息读取事件
     *
     * @param ctx 上线文对象
     * @param msg 客户端发送的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 转文本
        String text = msg.text();
        // 转为websocket请求对象(消息包装)
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        // 获取请求类型
        WSReqTypeEnum type = WSReqTypeEnum.of(wsBaseReq.getType());
        // 判断请求类型
        switch (type) {
            // 授权请求
            case AUTHORIZE:
                webSocketService.authorize(ctx.channel(), wsBaseReq.getData());
                break;
            // 心跳包请求
            case HEARTBEAT:
                break;
            // 登录请求
            case LOGIN:
                webSocketService.handleLoginReq(ctx.channel());
                break;
            default:
                log.info("未知类型");
                break;
        }
    }

    /**
     * 用户下线统一处理
     */
    private void userOffline(Channel channel) {
        // 删除连接
        webSocketService.offline(channel);
        // 连接关闭
        channel.close();
    }
}
