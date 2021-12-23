package com.tech.redis.lock.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author lw
 * @since 2021/12/4
 */
@Slf4j
@Service
public class LuaDistributeLock {
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;
    private static String LOCK_PREFIX = "lua_";
    private DefaultRedisScript<Boolean> lockScript;

//    @Scheduled(cron = "0/10 * * * * *")
    public void lockJob() {
        String lock = LOCK_PREFIX + "LuaDistributeLock";
        boolean luaRes = false;
        try {
            luaRes = luaExpress(lock, getHostIp());
            //获取锁失败
            if (!luaRes) {
                String value = (String) redisService.getValue(lock);
                log.info("lua get lock fail,lock belong to :{}", value);
                return;
            } else {
                log.info("lua start lock LuaDistributeLock success");
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            log.error("lock error", e);
        } finally {
            if (luaRes) {
                redisService.remove(lock);
                log.info("release lock success");
            }
        }
    }

    /**
     * 获取锁
     *
     * @param key
     * @param val
     * @return
     */
    public Boolean luaExpress(String key, String val) {
        lockScript = new DefaultRedisScript<Boolean>();
        lockScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("add.lua")
        ));
        lockScript.setResultType(Boolean.class);
        List<Object> keyList = new ArrayList<>();
        keyList.add(key);
        keyList.add(val);
        Boolean result = (Boolean) redisTemplate.execute(lockScript, keyList);
        return result;
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
