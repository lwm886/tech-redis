package com.tech.seckill.service.impl;

import com.tech.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lw
 * @since 2021/12/21
 */
@Service
public class SecKillServiceImpl implements SecKillService {
    
    //秒杀开始状态及时间（需初始化 0_秒杀开始时间戳【秒】）
    private static final String secStartPrefix="skuId_start_";
    
    //当前参与秒杀的流量数
    private static final String secAccess="skuId_access_";
    
    //流量限制总数（需初始化 根据需要可以设置成库存数的倍数，比如设置为库存数的1.5倍）
    private static final String secCount="skuId_count_";
    
    //布隆过滤器 防止重复秒杀
    private static final String bloomFilterName="userIdBloomFilter";
    
    //当前已购买数量（需初始化 0）
    private static final String secBuyCount="skuId_buy_";
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Override
    public String secKill(int uid, int skuId) {
        
        //判断秒杀是否开始  状态位_开始时间 [0 未开始，1 开始] 防止多节点流量倾斜，当状态位为1表示可以启动秒杀了
        String isStart = (String)redisTemplate.opsForValue().get(secStartPrefix + skuId);
        if(!StringUtils.hasLength(isStart)){
            return "活动未开始";
        }
        int startStatus = Integer.parseInt(isStart.split("_")[0]);
        if(startStatus==0){
            int planStartTime = Integer.parseInt(isStart.split("_")[1]);
            if(getNow()<planStartTime){
                return "活动未开始";
            }else{
                //开始秒杀
                redisTemplate.opsForValue().set(secStartPrefix + skuId,isStart.replaceFirst("0","1"));
            }
        }
        
        //流量拦截 读取限制流量 当前流量，判断当前流量是否达到限制流量，如果达到则进行拦截
        // 两次读一次判断这三个操作并没有设计成一个原子性操作，因为是限流，过滤掉大部分流量即可，以及考虑到执行的性能，并没有使用lua脚本做成原子性操作
        //信息校验层扣减库存采用了原子性操作
        String skuAccessName=secAccess+skuId; //当前参与秒杀的流量数
        String accessNumText = (String)redisTemplate.opsForValue().get(skuAccessName);
        Integer accessNum=StringUtils.hasLength(accessNumText)?Integer.parseInt(accessNumText):0;

        String countName = secCount + skuId; //最大允许的流量数
        int maxCount = Integer.parseInt((String)redisTemplate.opsForValue().get(countName));
        if(accessNum>maxCount){ //如果达到最大流量，进行限流
            return "抢购已经完成，欢迎下次参与";
        }else{
            redisTemplate.opsForValue().increment(skuAccessName);
        }
        
        //信息校验层
        if(redisIdExist(uid)){ //校验用户ID是否抢购过
            return "抢购已完成，欢迎下次参与";
        }else{ //如果没有抢购过，添加到布隆过滤器，下次不可抢购
            redisIdAdd(uid);
        }
        //校验库存（lua脚本原子性执行）防止超卖
        //读取当前购买数量，与初始化库存数比较，如果有剩余库存则抢购成功
        String currentBuyCountKey=secBuyCount+skuId;
        Boolean check = checkStock(currentBuyCountKey);
        if(check){
            return "恭喜您，抢购成功";
        }else{
            return "抢购已完成，欢迎下次参与";
        }
        //todo 完成校验后，将数据发送到MQ（异步 解耦 流量削峰）
        //todo 多个监听程序消费数据进行入库
    }

    private long getNow() {
        return System.currentTimeMillis()/1000;
    }

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
    
    Boolean checkStock(String key){
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("checkStock.lua")));
        script.setResultType(Boolean.class);
        List<Object> keyList = new ArrayList<>();
        keyList.add(key);
        Boolean res = (Boolean) redisTemplate.execute(script, keyList);
        return res;
    }
    
    
}
