package com.abin.mallchat.common.user.service;

import com.abin.mallchat.common.user.domain.entity.UserBackpack;
import com.abin.mallchat.common.user.domain.enums.IdempotentEnum;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-09-08
 */
public interface IUserBackpackService {

    /**
     * 给用户发放一个物品
     *
     * @param uid            用户id
     * @param itemId         物品id
     * @param idempotentEnum 幂等类型
     * @param businessId     幂等唯一标识
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
