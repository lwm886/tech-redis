package com.tech.redis.lock.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * @author lw
 * @since 2021/12/3
 */
@Slf4j
@Service
public class LockNxExJob {
    private static String LOCK_PREFIX="prefix_";
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private RedisService redisService;
    
//    @Scheduled(cron = "0/10 * * * * *")
    public void lockJob()  {
        String lock=LOCK_PREFIX+"LockNxExJob";
        Boolean locked =false;
        try {
            locked = redisTemplate.opsForValue().setIfAbsent(lock, getHostIp());
            //获取锁失败
            if(!locked){
                String o = (String)redisService.getValue(lock);
                log.info("get lock fail,lock belonging to:{}",o);
            }else{
                redisTemplate.expire(lock,3600, TimeUnit.SECONDS);
                log.info("start lock LockNxExJob success");
                //模拟业务处理
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            log.error("lock error",e);
        } finally {
            if(locked){
                redisService.remove(lock);
                log.info("release lock LockNxExJob success");
            }
        }
    }

    /**
     * 获取本机内网IP地址方法
     * @return
     */
    private static String getHostIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
