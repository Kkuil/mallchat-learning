package com.abin.mallchat.common.user.service.cache;

import com.abin.mallchat.common.user.dao.ItemConfigDao;
import com.abin.mallchat.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-09-09
 */
@Component
public class ItemCache {

    @Autowired
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item",key = "'itemsByType:'+#itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getByType(itemType);
    }

    @CacheEvict(cacheNames = "item",key = "'itemsByType:'+#itemType")
    public void evictByType(Integer itemType) {
    }
}
