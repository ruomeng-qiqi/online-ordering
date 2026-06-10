package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付DTO
 */
@Data
public class PaymentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付方式：1-微信支付
     */
    private Integer paymentMethod;

    /**
     * 使用的积分
     */
    private Integer pointsUsed;

    /**
     * 积分抵扣金额
     */
    private BigDecimal pointsDeduction;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 获得积分
     */
    private Integer pointsEarned;
}
