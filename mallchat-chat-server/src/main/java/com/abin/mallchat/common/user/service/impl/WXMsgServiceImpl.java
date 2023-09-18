package com.abin.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.UserService;
import com.abin.mallchat.common.user.service.WxMsgService;
import com.abin.mallchat.common.user.service.adapter.TextBuilder;
import com.abin.mallchat.common.user.service.adapter.UserAdapter;
import com.abin.mallchat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Kkuil
 * @Date 2023/08/05 12:30
 * @Description
 */
@Service
@Slf4j
public class WXMsgServiceImpl implements WxMsgService {

    /**
     * 微信事件前缀
     */
    public static final String WX_PREFIX_EVENT_KEY = "qrscene_";

    /**
     * 推送授权消息模板
     */
    public static final String WX_AUTH_MSG = "请点击登录：<a href='%s'>登录</a>";

    /**
     * 微信回调路径
     */
    public static final String WX_CALLBACK_PATH = "/wx/portal/public/callBack";
    @Resource
    private WebSocketService webSocketService;

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private WxMpService wxMpService;

    /**
     * openid和登录code的关系map
     */
    private static final ConcurrentHashMap<String, Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    @Value("${wx.mp.callback}")
    private String callback;

    /**
     * 微信授权回调地址
     */
    public static final String WX_AUTH_CALLBACK_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    /**
     * 微信扫码事件处理
     *
     * @param wxMpXmlMessage 微信xml消息
     * @return WxMpXmlOutMessage
     */
    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        // 获取扫码用户的openID
        String openId = wxMpXmlMessage.getFromUser();
        // 获取事件码
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)) {
            return null;
        }
        // 通过openId获取用户信息
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        // 用户已经注册并授权
        if (registered && authorized) {
            // 走登录成功逻辑 通过code找到给channel推送消息
            webSocketService.handleScanLoginSuccess(code, user.getId());
            return null;
        }
        // 用户未注册，就先注册
        if (!registered) {
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }
        // 推送链接让用户授权
        WAIT_AUTHORIZE_MAP.put(openId, code);
        webSocketService.waitAuthorize(code);
        String authorizeUrl = String.format(
                WX_AUTH_CALLBACK_URL,
                wxMpService.getWxMpConfigStorage().getAppId(),
                URLEncoder.encode(callback + WX_CALLBACK_PATH)
        );
        log.info("微信授权回调地址: {}", authorizeUrl);
        // 扫码之后推送给用户的授权信息
        String pushMsg = String.format(WX_AUTH_MSG, authorizeUrl);
        return new TextBuilder().build(pushMsg, wxMpXmlMessage, wxMpService);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        // 更新用户信息
        if (StrUtil.isBlank(user.getAvatar())) {
            fillUserInfo(user.getId(), userInfo);
        }
        // 通过code找到用户channel，进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        webSocketService.handleScanLoginSuccess(code, user.getId());
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(uid, userInfo);
        userDao.updateById(user);
    }

    /**
     * 从wxMpXmlMessage取出事件名
     *
     * @param wxMpXmlMessage wxMpXmlMessage
     * @return 事件key
     */
    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            // 去除前缀字符串
            String code = eventKey.replace(WX_PREFIX_EVENT_KEY, "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("getEventKey error eventKey:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }
}
