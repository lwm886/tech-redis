package com.tech.tech.redis.filter.controller;


import com.tech.tech.redis.filter.service.BloomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lw
 * @since 2021-12-20
 */
@RestController
@RequestMapping("/filter/sysUser")
public class SysUserController {
    
    @Autowired
    BloomFilterService bloomFilterService;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    private static final String bloomFilterName="userIdBloomFilter";
    
    /**
     * google bloom filter
     * @param id
     * @return
     */
    @GetMapping("id")
    Boolean id(long id){
        return bloomFilterService.userIdExists(id);
    }

    /**
     * redis布隆过滤器 添加数据到布隆过滤器
     * @param id
     * @return
     */
    @GetMapping("redis/idAdd")
    Boolean redisIdAdd(int id){
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("bloomFilterAdd.lua")));
        script.setResultType(Boolean.class);
        List<Object> keyList = new ArrayList<>();
        keyList.add(bloomFilterName);
        keyList.add(String.valueOf(id));
        Boolean res = (Boolean) redisTemplate.execute(script, keyList);
        return res;
    }

    /**
     * redis 布隆过滤器 判断数据是否一定不存在或者可能存在
     * @param id
     * @return
     */
    @GetMapping("redis/idExist")
    Boolean redisIdExist(int id){
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("bloomFilterExist.lua")));
        script.setResultType(Boolean.class);
        List<Object> keyList = new ArrayList<>();
        keyList.add(bloomFilterName);
        keyList.add(String.valueOf(id));
        Boolean res = (Boolean) redisTemplate.execute(script, keyList);
        return res;
    }
    
    
}
