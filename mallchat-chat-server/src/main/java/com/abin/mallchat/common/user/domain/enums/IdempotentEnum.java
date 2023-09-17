package com.abin.mallchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description
 */
@AllArgsConstructor
@Getter
public enum IdempotentEnum {

    /**
     * 用户ID
     */
    UID(1, "用户id"),
    /**
     * 消息ID
     */
    MSG_ID(2, "消息id"),
    ;

    private final Integer type;
    private final String desc;
}
