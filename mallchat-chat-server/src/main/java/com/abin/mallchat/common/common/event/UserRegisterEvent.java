package com.abin.mallchat.common.common.event;

import com.abin.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private User user;

    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
