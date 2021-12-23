package com.tech.redis.lock.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author lw
 * @since 2021/12/7
 */
@Slf4j
@Component
public class JedisDistribute {
    private static String LOCK_PREFIX="lua_";
    
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    
    @Autowired
    private RedisService redisService;
    
//    @Scheduled(cron = "0/10 * * * * *")
    public void lockJob() throws InterruptedException {
        String lock=LOCK_PREFIX+"JedisDistribute";

        boolean lockRes=false;
        
        try{
            lockRes=setLock(lock,getHostIp(),600);
            
            //获取锁失败
            if(!lockRes){
//                String value = new String(get(lock));
                String value= (String) redisService.getValue(lock);
                log.info("JedisDistribute get lock fail ,lock belong to:{}",value);
                return;
            }else{
                //获取锁成功
                log.info("JedisDistribute start lock success");
                Thread.sleep(5000);
            }
            
        }catch (Exception e){
            log.error("JedisDistribute lock error",e);
        }finally {
            if(lockRes){
                redisService.remove(lock);
                log.info("JedisDistribute release lock success");
            }
        }
        
        
    }
    
    public boolean setLock(String key,String val,long expire){
        try {
            Boolean res=redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.set(key.getBytes(),val.getBytes(), Expiration.seconds(expire), RedisStringCommands.SetOption.SET_IF_ABSENT);
                }
            });
            return res;
        } catch (Exception e) {
            log.error("set redis occured an exception",e);
        }
        return false;
    }
    
    
    

    /**
     * 获取本机内网IP地址方法
     *
     * @return
     */
    private static String getHostIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":") == -1) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
