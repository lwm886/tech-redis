package com.tech.redis.packet.entity;

import com.tech.redis.packet.entity.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 抢红包记录表，抢一个红包插	入一条记录
 * </p>
 *
 * @author lw
 * @since 2021-12-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RedPacketRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 抢到红包的金额
     */
    private Integer amount;

    /**
     * 抢到红包的用户的用户名
     */
    private String nickName;

    /**
     * 抢到红包的用户的头像
     */
    private String imgUrl;

    /**
     * 抢到红包用户的用户标识
     */
    private Integer uid;

    /**
     * 红包id，采用timestamp+5位随机	数
     */
    private Long redPacketId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
