package com.tech.tech.redis.filter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.tech.tech.redis.filter.mapper")
@SpringBootApplication
public class TechRedisFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechRedisFilterApplication.class, args);
    }

}
