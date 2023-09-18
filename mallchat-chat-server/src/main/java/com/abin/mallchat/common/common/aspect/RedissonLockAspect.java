package com.abin.mallchat.common.common.aspect;

import com.abin.mallchat.common.common.annotation.RedissonLock;
import com.abin.mallchat.common.common.service.DistributedLockService;
import com.abin.mallchat.common.common.utils.SpElUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description Redisson注解切面类
 */
@Component
@Aspect
@Order(0)
public class RedissonLockAspect {
    @Resource
    private DistributedLockService lockService;

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String prefix = StringUtils.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        String redisKey = prefix + ":" + key;
        return lockService.executeWithLock(redisKey, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }
}
