package com.abin.mallchat.common.user.service.handler;

import com.abin.mallchat.common.user.service.WxMsgService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author Kkuil
 * @Description 微信扫码处理器
 * @Date 2023/09/18
 */
@Component
public class ScanHandler extends AbstractHandler {
    @Resource
    private WxMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(
            WxMpXmlMessage wxMpXmlMessage,
            Map<String, Object> map,
            WxMpService wxMpService,
            WxSessionManager wxSessionManager
    ) {
        return wxMsgService.scan(wxMpXmlMessage);
    }

}
