package com.abin.mallchat.common.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @Author Kkuil
 * @Date 2023/08/05 12:30
 * @Description 
 */
public interface WXMsgService {
    /**
     * 用户扫码成功
     * @return
     */
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage);

    /**
     * 用户授权
     */
    void authorize(WxOAuth2UserInfo userInfo);
}
