package com.abin.mallchat.common.user.service;

import com.abin.mallchat.common.user.domain.entity.Role;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-09-10
 */
public interface IRoleService {

    /**
     * 是否拥有某个权限 临时写法
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
