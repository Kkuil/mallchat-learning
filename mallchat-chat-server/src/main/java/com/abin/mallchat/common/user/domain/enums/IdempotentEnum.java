package com.abin.mallchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-09-09
 */
@AllArgsConstructor
@Getter
public enum IdempotentEnum {

    UID(1, "uid"),
    MSG_ID(2, "消息id");
    private final Integer type;
    private final String desc;
}
