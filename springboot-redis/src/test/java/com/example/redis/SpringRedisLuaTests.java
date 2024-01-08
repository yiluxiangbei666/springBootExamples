package com.example.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.example.redis.configuration.RedisConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


@Slf4j
@SpringBootTest
@ContextConfiguration(classes = RedisConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class SpringRedisLuaTests {

    private static final String LUA_SCRIPT = "local lockParam = redis.call('exists', KEYS[1])\n" +
            "if lockParam == 0 then\n" +
            "redis.call('set', KEYS[1], ARGV[1])\n" +
            "redis.call('expire', KEYS[1], ARGV[2])\n" +
            "end\n" +
            "return lockParam\n";

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testLua() {
        Long orderId = IdUtil.getSnowflake().nextId();
        String lockKey = "order:"+orderId;
        try {
            Long lock = (Long) redisTemplate.execute(RedisScript.of(LUA_SCRIPT, Long.class), Arrays.asList(lockKey), "1", 30);
            if (lock == 0) {
                // 模拟业务执行10s
                TimeUnit.MILLISECONDS.sleep(10*1000);
                // 释放redis分布式锁
                redisTemplate.delete(lockKey);
            }
            log.info("lock:[{}]", lock);
        } catch (Exception e) {
            redisTemplate.delete(lockKey);
        }


    }


}
