package com.abin.mallchat.common.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import com.abin.mallchat.common.common.domain.vo.resp.ApiResult;
import com.abin.mallchat.common.common.exception.BusinessException;
import com.abin.mallchat.common.common.utils.JsonUtils;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.IpDetail;
import com.abin.mallchat.common.user.domain.entity.IpInfo;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.IpService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description
 */
@Service
@Slf4j
public class IpServiceImpl implements IpService, DisposableBean {

    /**
     * 线程池
     */
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", false)
    );

    /**
     * 每次获取IP详情的间隔时间
     */
    public static final int INTERVAL_TIME_PER_GET_IP_DETAIL = 2000;

    /**
     * 获取IP详情的URL模板
     */
    public static final String GET_IP_DETAIL_URL = "https://ip.taobao.com/outGetIpInfo?ip=%s&accessKey=alibaba-inc";

    /**
     * 线程停止前的最大等待时间（为了实现优雅停机），到了时间直接停止所有任务
     */
    public static final int MAX_WAIT_TIME_BEFORE_SHUT_DOWN_THREAD_POOL = 30;

    /**
     * 获取IP归属地详情时的最大重试次数
     */
    private static final int MAX_RETRY_COUNT_GET_IP_DETAIL = 5;

    @Resource
    private UserDao userDao;

    /**
     * 异步刷新当前用户IP详情
     *
     * @param uid 用户ID
     */
    @Override
    public void refreshIpDetailAsync(Long uid) {
        EXECUTOR.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (Objects.isNull(ipInfo)) {
                return;
            }
            String ip = ipInfo.needRefreshIp();
            if (StringUtils.isBlank(ip)) {
                return;
            }
            IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes(ip);
            if (Objects.nonNull(ipDetail)) {
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
            }
        });
    }

    /**
     * 获取IP归属地详情
     *
     * @param ip ip地址
     * @return IP归属地详情
     */
    @Retryable(value = RuntimeException.class, maxAttempts = MAX_RETRY_COUNT_GET_IP_DETAIL, backoff = @Backoff(delay = INTERVAL_TIME_PER_GET_IP_DETAIL))
    private static IpDetail tryGetIpDetailOrNullTreeTimes(String ip) {
        String url = String.format(GET_IP_DETAIL_URL, ip);
        String result = HttpUtil.get(url);
        ApiResult<IpDetail> ipResult = JsonUtils.toObj(result, new TypeReference<ApiResult<IpDetail>>() {
        });
        IpDetail detail = ipResult.getData();
        if (Objects.isNull(detail)) {
            throw new BusinessException("重试");
        }
        return detail;
    }

    /**
     * Bean销毁时
     */
    @Override
    public void destroy() throws Exception {
        // 优雅停机（不是直接把所有线程停止，而是停止线程的调度）
        EXECUTOR.shutdown();
        // 最多等30秒，处理不完就拉倒
        if (!EXECUTOR.awaitTermination(MAX_WAIT_TIME_BEFORE_SHUT_DOWN_THREAD_POOL, TimeUnit.SECONDS)) {
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
            }
        }
    }
}
