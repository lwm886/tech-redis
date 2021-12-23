package com.tech.seckill.controller;

import com.tech.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lw
 * @since 2021/12/21
 */
@RestController
public class SecKillController {
    
    @Autowired
    private SecKillService secKillService;
    
    @GetMapping("secKill")
    String secKill(int uid,int skuId){
        return secKillService.secKill(uid,skuId);
    }
    
}
