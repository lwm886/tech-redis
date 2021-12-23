package com.tech.redis.packet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author lw
 * @since 2021/12/17
 */
@Data
public class BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
}
