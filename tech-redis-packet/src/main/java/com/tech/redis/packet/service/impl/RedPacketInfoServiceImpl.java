package com.tech.redis.packet.service.impl;

import com.tech.redis.packet.entity.RedPacketInfo;
import com.tech.redis.packet.mapper.RedPacketInfoMapper;
import com.tech.redis.packet.service.IRedPacketInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 红包信息表，新建一个红包插	入一条记录 服务实现类
 * </p>
 *
 * @author lw
 * @since 2021-12-17
 */
@Service
public class RedPacketInfoServiceImpl extends ServiceImpl<RedPacketInfoMapper, RedPacketInfo> implements IRedPacketInfoService {

}
