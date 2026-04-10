package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 积分记录
 */
@Data
public class PointsRecord implements Serializable {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 积分变动（正数为增加，负数为减少）
     */
    private Integer pointsChange;

    /**
     * 变动后积分余额
     */
    private Integer pointsBalance;

    /**
     * 变动类型：EARN(消费获得), REDEEM(兑换使用), EXPIRE(过期), ADMIN_ADJUST(管理员调整)
     */
    private String changeType;

    /**
     * 变动说明
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
