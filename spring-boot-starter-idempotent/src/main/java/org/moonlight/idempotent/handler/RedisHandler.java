package org.moonlight.idempotent.handler;

import org.moonlight.idempotent.exception.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Moonlight
 */
@Component
public class RedisHandler {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 功能描述: 将键值数据写入redis缓存
     * @param key 健
     * @param value 值
     * @return boolean 设置值是否成功 true 成功  false 失败
     * @author moonlight
     **/
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }

    /**
     * 功能描述: 将键值数据写入redis缓存，并设置过期时间
     * @param key 健
     * @param value 值
     * @param expireTime 过期时间，单位毫秒
     * @return boolean 设置值是否成功 true 成功  false 失败
     * @author moonlight
     **/
    public boolean setExpireTimeMilliseconds(String key, Object value, Long expireTime) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }

    /**
     * 功能描述: 判断redis中是否存在指定的key
     * @param key 健
     * @return boolean true 有值  false 没有对应的key数据
     * @author moonlight
     **/
    public boolean exists(String key) {
        try {
            Boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey == null) {
                throw new CacheException(" redis connection has problem exists key "  + key + " failed ");
            }
            return hasKey;
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }

    /**
     * 功能描述: 从redis中获取指定key的数据
     * @param key 健
     * @return T 返回数据
     * @author moonlight
     **/
    public <T> T get(String key, Class<T> t) {
        try {
            Object res = redisTemplate.opsForValue().get(key);
            if (res == null) {
                return null;
            }
            return (T) res;
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }

    /**
     * 功能描述: 从redis中删除指定key的数据
     * @param key 健
     * @return boolean 删除数据是否成功, true 删除成功, false 删除失败
     * @author moonlight
     **/
    public boolean del(String key) {
        try {
            if (exists(key)) {
                Boolean delete = redisTemplate.delete(key);
                if (delete == null) {
                    throw new CacheException(" redis connection has problem delete key " + key + " failed ");
                }
                return delete;
            }
            return false;
        } catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }
}
