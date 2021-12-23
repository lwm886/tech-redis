package com.tech.redis.packet.service.impl;

import com.tech.redis.packet.entity.RedPacketRecord;
import com.tech.redis.packet.mapper.RedPacketRecordMapper;
import com.tech.redis.packet.service.IRedPacketRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 抢红包记录表，抢一个红包插	入一条记录 服务实现类
 * </p>
 *
 * @author lw
 * @since 2021-12-17
 */
@Service
public class RedPacketRecordServiceImpl extends ServiceImpl<RedPacketRecordMapper, RedPacketRecord> implements IRedPacketRecordService {

}
