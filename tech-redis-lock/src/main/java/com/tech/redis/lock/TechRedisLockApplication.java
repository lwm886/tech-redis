package com.tech.redis.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TechRedisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechRedisLockApplication.class, args);
    }

}
