package com.abin.mallchat.common.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @Author Kkuil
 * @Date 2023/08/05 12:30
 * @Description
 */
public interface WxMsgService {
    /**
     * 用户扫码成功
     *
     * @param wxMpXmlMessage 微信xml消息
     * @return 扫码结果
     */
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage);

    /**
     * 用户授权
     *
     * @param userInfo 用户信息
     */
    void authorize(WxOAuth2UserInfo userInfo);
}
