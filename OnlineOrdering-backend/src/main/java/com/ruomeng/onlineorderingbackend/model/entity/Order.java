package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 */
@Data
public class Order implements Serializable {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 桌号
     */
    private String tableNumber;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 微信用户OpenID
     */
    private String openId;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 本单获得积分
     */
    private Integer pointsEarned;

    /**
     * 本单使用积分
     */
    private Integer pointsUsed;

    /**
     * 订单状态：PENDING(待处理), PROCESSING(制作中), COMPLETED(已完成), CANCELLED(已取消), PAID(已支付)
     */
    private String status;

    /**
     * 支付方式：WECHAT(微信支付), CASH(现金), CARD(银行卡)
     */
    private String paymentMethod;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
