package com.abin.mallchat.common.common.event.listener;

import com.abin.mallchat.common.common.event.UserRegisterEvent;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.IdempotentEnum;
import com.abin.mallchat.common.user.domain.enums.ItemEnum;
import com.abin.mallchat.common.user.service.IUserBackpackService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description
 */
@Component
public class UserRegisterListener {

    /**
     * 前十名
     */
    public static final int TOP_TEN = 10;

    /**
     * 前一百名
     */
    public static final int TOP_ONE_HUNDRED = 100;

    @Resource
    private IUserBackpackService userBackpackService;
    @Resource
    private UserDao userDao;

    /**
     * 监听用户注册事件（事务后执行）
     *
     * @param event 事件对象
     */
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        // 发放改名卡
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    /**
     * 监听用户注册事件（事务后执行）
     *
     * @param event 事件对象
     */
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        // 获取当前注册人数
        int registeredCount = userDao.count();
        // 前十名发放前十名注册徽章
        if (registeredCount < TOP_TEN) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
        // 前一百名名发放前一百名注册徽章
        if (registeredCount < TOP_ONE_HUNDRED) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }

    }
}
