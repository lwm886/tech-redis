package com.tech.redis.packet.entity;

import com.tech.redis.packet.entity.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 红包信息表，新建一个红包插	入一条记录
 * </p>
 *
 * @author lw
 * @since 2021-12-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RedPacketInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 红包id，采用timestamp+5位随机数
     */
    private Long redPacketId;

    /**
     * 红包总金额，单位分
     */
    private Integer totalAmount;

    /**
     * 红包总个数
     */
    private Integer totalPacket;

    /**
     * 剩余红包金额，单位分
     */
    private Integer remainingAmount;

    /**
     * 剩余红包个数
     */
    private Integer remainingPacket;

    /**
     * 新建红包用户的用户标识
     */
    private Integer uid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
