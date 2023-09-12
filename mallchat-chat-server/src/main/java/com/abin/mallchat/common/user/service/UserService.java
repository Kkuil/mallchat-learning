package com.abin.mallchat.common.user.service;

import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.vo.req.BlackReq;
import com.abin.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.abin.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-08-27
 */
public interface UserService {

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, Long itemId);

    void black(BlackReq req);
}
