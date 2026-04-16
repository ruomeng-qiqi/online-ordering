package com.ruomeng.onlineorderingbackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情VO
 */
@Data
public class OrderDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 顾客昵称
     */
    private String customerName;

    /**
     * 是否会员：0-否，1-是
     */
    private Integer isMember;

    /**
     * 餐台ID
     */
    private Long tableId;

    /**
     * 餐台号
     */
    private String tableNumber;

    /**
     * 餐台名称
     */
    private String tableName;

    /**
     * 结账时间
     */
    private LocalDateTime checkoutTime;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 积分抵扣金额
     */
    private BigDecimal pointsDeduction;

    /**
     * 使用的积分数
     */
    private Integer pointsUsed;

    /**
     * 获得的积分数
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

    /**
     * 订单明细列表
     */
    private List<OrderItemVO> orderDetails;

    /**
     * 订单明细VO
     */
    @Data
    public static class OrderItemVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private Long dishId;
        private Long setmealId;
        private String name;
        private String image;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal amount;
        private String flavor;
    }
}
