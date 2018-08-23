package com.dev.main.shiro.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.SerializationUtils;

import java.util.*;

public class ShiroRedisCache<K, V> implements Cache<K, V> {

    private RedisTemplate redisTemplate;
    private String prefix = "shiro_redis";

    public ShiroRedisCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ShiroRedisCache(RedisTemplate redisTemplate, String prefix) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
    }

    @Override
    public V get(K k) throws CacheException {
        if (k == null) {
            return null;
        }
        byte[] bytes = getBytesKey(k);
        return (V) redisTemplate.opsForValue().get(bytes);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        if (k == null || v == null) {
            return null;
        }
        byte[] bytes = getBytesKey(k);
        redisTemplate.opsForValue().set(bytes, v);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        if(k==null){
            return null;
        }
        byte[] bytes =getBytesKey(k);
        V v = (V)redisTemplate.opsForValue().get(bytes);
        redisTemplate.delete(bytes);
        return v;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public int size() {
        return redisTemplate.getConnectionFactory().getConnection().dbSize().intValue();
    }

    @Override
    public Set<K> keys() {
        byte[] bytes = (prefix+"*").getBytes();
        Set<byte[]> keys = redisTemplate.keys(bytes);
        Set<K> sets = new HashSet<>();
        for (byte[] key:keys) {
            sets.add((K)key);
        }
        return sets;
    }

    @Override
    public Collection<V> values() {
        Set<K> keys = keys();
        List<V> values = new ArrayList<>(keys.size());
        for(K k :keys){
            values.add(get(k));
        }
        return values;
    }

    private byte[] getBytesKey(K key) {
        if (key instanceof String) {
            String prekey = this.prefix + key;
            return prekey.getBytes();
        } else {
            return SerializationUtils.serialize(key);
        }
    }


    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
