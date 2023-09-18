package com.abin.mallchat.common.user.service.handler;

import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.user.service.adapter.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * @Author Kkuil
 * @Description 微信工作号收到消息处理器
 * @Date 2023/09/18
 */
@Component
@Slf4j
public class MsgHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(
            WxMpXmlMessage wxMessage,
            Map<String, Object> context,
            WxMpService weixinService,
            WxSessionManager sessionManager
    ) {
        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            // 可以选择将消息保存到本地
        }
        try {
            // 是否是指定消息前缀
            // 当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
            boolean isSpecialMsg = StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服");
            if (isSpecialMsg && weixinService.getKefuService().kfOnlineList()
                    .getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        // 组装回复消息
        String content = "收到信息内容：" + JSONUtil.toJsonStr(wxMessage);
        log.info("content: {}", content);
        return new TextBuilder().build(content, wxMessage, weixinService);
    }

}
