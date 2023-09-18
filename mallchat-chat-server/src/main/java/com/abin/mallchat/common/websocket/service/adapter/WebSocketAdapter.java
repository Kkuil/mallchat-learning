package com.abin.mallchat.common.websocket.service.adapter;

import com.abin.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.websocket.domain.enums.WsRespTypeEnum;
import com.abin.mallchat.common.websocket.domain.vo.resp.WsBaseResp;
import com.abin.mallchat.common.websocket.domain.vo.resp.WSBlack;
import com.abin.mallchat.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.abin.mallchat.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * @Author Kkuil
 * @Date 2023/08/05 12:30
 * @Description 
 */
public class WebSocketAdapter {
    public static WsBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WsBaseResp<WSLoginUrl> resp = new WsBaseResp<>();
        resp.setType(WsRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    public static WsBaseResp<?> buildResp(User user, String token, boolean power) {
        WsBaseResp<WSLoginSuccess> resp = new WsBaseResp<>();
        resp.setType(WsRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess build = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(power ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                .build();
        resp.setData(build);
        return resp;
    }

    public static WsBaseResp<?> buildWaitAuthorizeResp() {
        WsBaseResp<WSLoginUrl> resp = new WsBaseResp<>();
        resp.setType(WsRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    public static WsBaseResp<?> buildInvalidTokenResp() {
        WsBaseResp<WSLoginUrl> resp = new WsBaseResp<>();
        resp.setType(WsRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }

    public static WsBaseResp<?> buildBlack(User user) {
        WsBaseResp<WSBlack> resp = new WsBaseResp<>();
        resp.setType(WsRespTypeEnum.BLACK.getType());
        WSBlack build = WSBlack.builder()
                .uid(user.getId())
                .build();
        resp.setData(build);
        return resp;
    }
}
