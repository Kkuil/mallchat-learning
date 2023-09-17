package com.abin.mallchat.common.common.event;

import com.abin.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 用户上线事件
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private final User user;

    public UserOnlineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
