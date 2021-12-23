package com.tech.redis.packet.controller;


import com.tech.redis.packet.entity.RedPacketInfo;
import com.tech.redis.packet.entity.RedPacketRecord;
import com.tech.redis.packet.mapper.RedPacketInfoMapper;
import com.tech.redis.packet.mapper.RedPacketRecordMapper;
import com.tech.redis.packet.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * <p>
 * 红包信息表，新建一个红包插	入一条记录 前端控制器
 * </p>
 *
 * @author lw
 * @since 2021-12-17
 */
@RestController
@RequestMapping("redPacketInfo")
public class RedPacketInfoController {
    
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    
    @Autowired
    RedPacketInfoMapper redPacketInfoMapper;
    
    @Autowired
    RedPacketRecordMapper redPacketRecordMapper;
    
    private IdWorker idWorker=new IdWorker(1,1,1);

    /**
     * 创建红包
     * @param uid 用户ID
     * @param totalNum 红包数
     * @param totalAmount 红包金额
     * @return
     */
    @RequestMapping("save")
    String saveRedPacket(Integer uid,Integer totalNum,Integer totalAmount){
        RedPacketInfo info = new RedPacketInfo();
        info.setUid(uid);
        info.setTotalAmount(totalAmount);
        info.setTotalPacket(totalNum);
        info.setRemainingPacket(totalNum);
        info.setRemainingAmount(totalAmount);
        info.setCreateTime(LocalDateTime.now());
        long redPacketId = idWorker.nextId();
        info.setRedPacketId(redPacketId);
        redPacketInfoMapper.insert(info);
        redisTemplate.opsForValue().set(redPacketId+"_total_num",totalNum+"");
        redisTemplate.opsForValue().set(redPacketId+"_total_amount",totalAmount+"");
        return "success";
    }

    /**
     * 抢红包
     * @param redPacketId 红包ID
     * @return
     */
    @RequestMapping("grab")
    Integer grab(long redPacketId){
        String n = redisTemplate.opsForValue().get(redPacketId + "_total_num");
        if(n!=null && Integer.valueOf(n)>0){
            return Integer.valueOf(n);
        }
        return 0;
    }

    /**
     * 拆红包
     * @param uid
     * @param redPacketId
     * @return
     */
    @RequestMapping("get")
    String get(int uid,long redPacketId){
        Integer randomAmount=0;
        String num = redisTemplate.opsForValue().get(redPacketId + "_total_num");
        if(!StringUtils.hasLength(num) || Integer.parseInt(num)==0){
            return "抱歉！红包已经抢完了";
        }
        String totalAmount = redisTemplate.opsForValue().get(redPacketId + "_total_amount");
        if(StringUtils.hasLength(totalAmount)){
            int ta = Integer.parseInt(totalAmount);
            int n=Integer.parseInt(num);
            Integer maxMoney=ta/n*2;
            Random random = new Random();
            randomAmount = random.nextInt(maxMoney);
        }
        updateInDB(uid,redPacketId,randomAmount);
        redisTemplate.opsForValue().decrement(redPacketId + "_total_num");
        redisTemplate.opsForValue().decrement(redPacketId + "_total_amount",randomAmount);
        return randomAmount+"";
    }
    
    String updateInDB(Integer uid,long redPacketId,int amount){
        RedPacketRecord record = new RedPacketRecord();
        record.setUid(uid);
        record.setRedPacketId(redPacketId);
        record.setAmount(amount);
        record.setCreateTime(LocalDateTime.now());
        redPacketRecordMapper.insert(record);
        return "success";
    }
}
