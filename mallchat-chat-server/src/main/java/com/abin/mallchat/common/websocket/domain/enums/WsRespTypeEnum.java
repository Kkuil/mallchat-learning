package com.abin.mallchat.common.websocket.domain.enums;

import com.abin.mallchat.common.websocket.domain.vo.resp.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Kkuil
 * @Date 2023/9/18
 * @Description websocket返回类型枚举
 */
@AllArgsConstructor
@Getter
public enum WsRespTypeEnum {
    /**
     * 登录二维码返回
     */
    LOGIN_URL(1, "登录二维码返回", WSLoginUrl.class),

    /**
     * 用户扫描成功等待授权
     */
    LOGIN_SCAN_SUCCESS(2, "用户扫描成功等待授权", null),

    /**
     * 用户登录成功返回用户信息
     */
    LOGIN_SUCCESS(3, "用户登录成功返回用户信息", WSLoginSuccess.class),

    /**
     * 新消息
     */
    MESSAGE(4, "新消息", WSMessage.class),

    /**
     * 上下线通知
     */
    ONLINE_OFFLINE_NOTIFY(5, "上下线通知", WSOnlineOfflineNotify.class),

    /**
     * 使前端的token失效，意味着前端需要重新登录
     */
    INVALIDATE_TOKEN(6, "使前端的token失效，意味着前端需要重新登录", null),

    /**
     * 拉黑用户
     */
    BLACK(7, "拉黑用户", WSBlack.class),

    /**
     * 消息标记
     */
    MARK(8, "消息标记", WSMsgMark.class),

    /**
     * 消息撤回
     */
    RECALL(9, "消息撤回", WSMsgRecall.class),

    /**
     * 好友申请
     */
    APPLY(10, "好友申请", WSFriendApply.class),

    /**
     * 成员变动
     */
    MEMBER_CHANGE(11, "成员变动", WSMemberChange.class),
    ;

    private final Integer type;
    private final String desc;
    private final Class dataClass;

    private static final Map<Integer, WsRespTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(WsRespTypeEnum.values()).collect(Collectors.toMap(WsRespTypeEnum::getType, Function.identity()));
    }

    public static WsRespTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
