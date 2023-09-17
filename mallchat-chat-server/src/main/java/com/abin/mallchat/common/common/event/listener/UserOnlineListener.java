package com.abin.mallchat.common.common.event.listener;

import com.abin.mallchat.common.common.event.UserOnlineEvent;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.UserActiveStatusEnum;
import com.abin.mallchat.common.user.service.IpService;
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
public class UserOnlineListener {

    @Resource
    private IpService ipService;
    @Resource
    private UserDao userDao;

    /**
     * 监听用户上线事件，更新数据库（fallbackExecution参数代表，外层没有事务时也要触发）
     *
     * @param event 事件对象
     */
    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void updateDb(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(UserActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        // 用户ip详情的解析
        ipService.refreshIpDetailAsync(user.getId());
    }
}
