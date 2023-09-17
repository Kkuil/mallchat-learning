package com.abin.mallchat.common.user.service.cache;

import com.abin.mallchat.common.user.dao.BlackDao;
import com.abin.mallchat.common.user.dao.UserRoleDao;
import com.abin.mallchat.common.user.domain.entity.Black;
import com.abin.mallchat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 用户缓存
 */
@Component
public class UserCache {
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private BlackDao blackDao;

    /**
     * 用户角色缓存
     *
     * @param uid 用户ID
     * @return 用户角色ID集合
     */
    @Cacheable(cacheNames = "user", key = "'roles:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    /**
     * 缓存黑名单
     *
     * @return 黑名单列表
     */
    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        collect.forEach((type, list) -> {
            result.put(type, list.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return result;
    }

    /**
     * 清除黑名单缓存
     *
     * @return
     */
    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

}
