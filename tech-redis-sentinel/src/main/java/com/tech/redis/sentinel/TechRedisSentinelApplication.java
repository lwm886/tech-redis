package com.tech.redis.sentinel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class TechRedisSentinelApplication {
    
    @Autowired
    RedisTemplate redisTemplate;
    
    public static void main(String[] args) {
        SpringApplication.run(TechRedisSentinelApplication.class, args);
    }
    
    @GetMapping("test")
    Object test(){
        redisTemplate.opsForValue().set("k1",100);
        return redisTemplate.opsForValue().get("k1");
    }
}
