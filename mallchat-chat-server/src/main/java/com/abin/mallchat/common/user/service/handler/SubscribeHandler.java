package com.abin.mallchat.common.user.service.handler;

import com.abin.mallchat.common.user.service.WxMsgService;
import com.abin.mallchat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author Kkuil
 * @Description 微信订阅处理器
 * @Date 2023/09/18
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Resource
    private WxMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(
            WxMpXmlMessage wxMessage,
            Map<String, Object> context,
            WxMpService weixinService,
            WxSessionManager sessionManager
    ) {
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());
        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = wxMsgService.scan(wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        if (responseResult != null) {
            return responseResult;
        }
        return new TextBuilder().build("感谢关注", wxMessage, weixinService);
    }

}
