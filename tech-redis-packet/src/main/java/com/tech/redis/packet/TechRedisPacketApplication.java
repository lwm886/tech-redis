package com.tech.redis.packet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.tech.redis.packet.mapper")
@SpringBootApplication
public class TechRedisPacketApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechRedisPacketApplication.class, args);
    }

}
