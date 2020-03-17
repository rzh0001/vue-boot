package org.jeecg.common.util;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/6 10:58
 */
@Service
@Slf4j
public class GuavaCacheUtils {
    /**
     * 缓存项的最大数目
     */
    private static final Long MAXIMUM_SIZE = 1000L;
    /**
     * 缓存项在给定时间范围内没有读/写访问，那么下次访问时，会被回收
     */
    private static final Long EXPIRE_AFTER_ACCESS = 3L;

    private final static Cache<String, Object> LOCAL_CACHE = CacheBuilder.newBuilder().maximumSize(MAXIMUM_SIZE)
        .expireAfterAccess(EXPIRE_AFTER_ACCESS, TimeUnit.SECONDS).build();

    /**
     * 获取缓存
     * 
     * @param key
     * @return
     */
    public static Object getStrValue(final String key) {
        String value = null;
        try {
            value = (String)LOCAL_CACHE.getIfPresent(key);
        } catch (Exception e) {
            log.error("guava 获取本地缓存异常，异常信息为：{}", e);
        }
        return Optional.fromNullable(value).or(Strings.EMPTY);
    }

    /**
     * 设置缓存
     * 
     * @param key
     * @param value
     */
    public static void setValue(final String key, final Object value) {
        try {
            LOCAL_CACHE.put(key, value);
        } catch (Exception e) {
            log.error("==>guava 设置本地缓存异常，异常信息为：{}", e);
        }
    }

    /**
     * 验证缓存是否已经存在
     * 
     * @param key
     * @param value
     * @return
     */
    public static boolean alreadyExists(final String key, final Object value) {
        if (StringUtils.isEmpty(GuavaCacheUtils.getStrValue(key))) {
            GuavaCacheUtils.setValue(key, value);
            return false;
        }
        return true;
    }
}
