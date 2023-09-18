package com.abin.mallchat.common.user.controller;

import com.abin.mallchat.common.user.service.WxMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;


/**
 * @Author Kkuil
 * @Description 微信控制器
 * @Date 2023/09/18
 */
@Slf4j
@RestController
@RequestMapping("wx/portal/public")
public class WxPortalController {

    /**
     * 微信AES加密类型
     */
    public static final String WX_ENC_TYPE_AES = "aes";
    @Resource
    private WxMpService wxMpService;
    @Resource
    private WxMpService wxService;
    @Resource
    private WxMpMessageRouter messageRouter;
    @Resource
    private WxMsgService wxMsgService;

    /**
     * 微信公众号验签接口
     *
     * @param signature     签名：防止恶意篡改
     * @param timestamp     时间戳：控制请求有效期
     * @param nonce         唯一值：防止重放攻击
     * @param echostr：随机字符串
     * @return echostr
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr
    ) {
        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "非法请求";
    }

    /**
     * 用户回调
     *
     * @param code 登录码
     * @return 重定向的地址
     * @throws WxErrorException 微信异常
     */
    @GetMapping("/callBack")
    public RedirectView callBack(@RequestParam String code) throws WxErrorException {
        // 利用微信登录的码进行换取access_token
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        // 再利用access_token去获取用户信息
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, "zh_CN");
        // 然后授权登录
        wxMsgService.authorize(userInfo);
        // 重定向抹茶
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("www.mallchat.cn");
        return redirectView;
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(
            @RequestBody String requestBody,
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("openid") String openid,
            @RequestParam(name = "encrypt_type", required = false) String encType,
            @RequestParam(name = "msg_signature", required = false) String msgSignature
    ) {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        // 验签
        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }
            out = outMessage.toXml();
        }
        if (WX_ENC_TYPE_AES.equalsIgnoreCase(encType)) {
            // 解密aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }
            out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
        }
        log.debug("\n组装回复信息：{}", out);
        return out;
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.messageRouter.route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }
        return null;
    }
}
