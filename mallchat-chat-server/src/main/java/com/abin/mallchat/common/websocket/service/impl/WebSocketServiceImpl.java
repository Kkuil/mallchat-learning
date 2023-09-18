package com.abin.mallchat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.event.UserOnlineEvent;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.service.IRoleService;
import com.abin.mallchat.common.user.service.LoginService;
import com.abin.mallchat.common.websocket.NettyUtil;
import com.abin.mallchat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.abin.mallchat.common.websocket.domain.vo.resp.WsBaseResp;
import com.abin.mallchat.common.websocket.service.WebSocketService;
import com.abin.mallchat.common.websocket.service.adapter.WebSocketAdapter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Kkuil
 * @Date 202309/17 17:00:00
 * @Description 专门管理websocket的逻辑，包括推拉
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Resource
    @Lazy
    private WxMpService wxMpService;
    @Resource
    private UserDao userDao;
    @Resource
    private LoginService loginService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private IRoleService iRoleService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 最多等待用户点击登录的时间
     */
    public static final Duration DURATION = Duration.ofHours(1);

    /**
     * 保存的最大数量
     */
    public static final int MAXIMUM_SIZE = 1000;

    /**
     * 存储所有用户的连接（登录态/游客）
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 临时保存登录code和channel的映射关系
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    /**
     * 连接
     *
     * @param channel 连接通道
     */
    @Override
    public void online(Channel channel) {
        // 连接，即保存当前channel信息
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 处理登录请求
     *
     * @param channel 连接通道
     */
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        // 生成随机码
        Integer code = generateLoginCode(channel);
        // 请求微信申请带参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        // 把码推送给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    /**
     * 用户下线
     *
     * @param channel 断开连接
     */
    @Override
    public void offline(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        // todo 用户下线
    }

    /**
     * 处理登录成功事件
     *
     * @param code 登录码
     * @param uid  用户ID
     */
    @Override
    public void handleScanLoginSuccess(Integer code, Long uid) {
        // 确认链接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(uid);
        // 移除临时登录缓存
        WAIT_LOGIN_MAP.invalidate(code);
        // 调用登录模块获取token
        String token = loginService.login(uid);
        // 用户登录
        loginSuccess(channel, user, token);
    }

    /**
     * 等待用户点击授权
     *
     * @param code 登录码
     */
    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        // 一旦用户点击了微信公众号推送的登录按钮，就返回给前端登录成功
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    /**
     * 授权
     *
     * @param channel 当前通道
     * @param token   用户token
     */
    @Override
    public void authorize(Channel channel, String token) {
        // 解析token，获取token中的uid
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            User user = userDao.getById(validUid);
            loginSuccess(channel, user, token);
        } else {
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    /**
     * 向所有人推送消息
     *
     * @param msg 需要推送的消息
     */
    @Override
    public void sendMsgToAll(WsBaseResp<?> msg) {
        // 利用线程池进行推送
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, msg));
        });
    }

    /**
     * 登录成功事件
     *
     * @param channel 当前通道
     * @param user    用户对象
     * @param token   token
     */
    private void loginSuccess(Channel channel, User user, String token) {
        // 保存channel的对应uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        // 推送成功消息
        sendMsg(channel, WebSocketAdapter.buildResp(user, token, iRoleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        user.setLastOptTime(new Date());
        // 用户登录成功，刷新IP
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        // 用户上线成功的事件
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }

    /**
     * 推送消息
     *
     * @param channel 当前通道
     * @param resp    消息体
     */
    private void sendMsg(Channel channel, WsBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    /**
     * 生成随机登录码，用于生成随机二维码
     *
     * @param channel 当前通道
     * @return 随机登录码
     */
    private Integer generateLoginCode(Channel channel) {
        int code = 0;
        boolean isNotExist = Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel));
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (isNotExist);
        return code;
    }
}
