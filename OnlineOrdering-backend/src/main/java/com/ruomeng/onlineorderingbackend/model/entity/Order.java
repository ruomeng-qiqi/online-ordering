package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 餐台ID
     */
    private Long tableId;

    /**
     * 结账时间
     */
    private LocalDateTime checkoutTime;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 积分抵扣金额
     */
    private BigDecimal pointsDeduction;

    /**
     * 使用的积分
     */
    private Integer pointsUsed;

    /**
     * 获得的积分
     */
    private Integer pointsEarned;

    /**
     * 支付方式：1-在线支付，2-线下支付
     */
    private Integer paymentMethod;

    /**
     * 订单状态：1-待支付，2-已完成，3-已取消
     */
    private Integer orderStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
