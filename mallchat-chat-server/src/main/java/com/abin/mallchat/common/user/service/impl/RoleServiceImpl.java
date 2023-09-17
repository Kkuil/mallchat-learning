package com.abin.mallchat.common.user.service.impl;

import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.service.IRoleService;
import com.abin.mallchat.common.user.service.cache.UserCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description
 */
@Service
public class RoleServiceImpl implements IRoleService {
    @Resource
    private UserCache userCache;

    /**
     * 判断是否有管理员权限
     *
     * @param uid      用户ID
     * @param roleEnum 角色枚举
     * @return 是否有管理员权限
     */
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains((roleEnum.getId()));
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}
