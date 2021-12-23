package com.tech.tech.redis.filter.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.tech.tech.redis.filter.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author lw
 * @since 2021/12/20
 */
@Slf4j
@Service
public class BloomFilterService {
    
    //google bloom filter 基于JVM内存的布隆过滤器
    private BloomFilter<Long> bloomFilter;
    
    @Autowired
    ISysUserService sysUserService;
    
    @PostConstruct
    void  init(){
        List<SysUser> list = sysUserService.list();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        bloomFilter=BloomFilter.create(Funnels.longFunnel(),list.size());
        list.forEach(sysUser -> {
            bloomFilter.put(sysUser.getId());
        });
        log.info("init bloom filter OK");

    }
    
    public boolean userIdExists(Long id){
        return bloomFilter.mightContain(id);
    }
}
