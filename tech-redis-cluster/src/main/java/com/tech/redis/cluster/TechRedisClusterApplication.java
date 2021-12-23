package com.tech.redis.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class TechRedisClusterApplication {
    
    @Autowired
    RedisTemplate redisTemplate;
    
    public static void main(String[] args) {
        SpringApplication.run(TechRedisClusterApplication.class, args);
    }
    
    @GetMapping("test")
    Object test(){
        redisTemplate.opsForValue().set("test","100");
        return redisTemplate.opsForValue().get("test");
    }
}
