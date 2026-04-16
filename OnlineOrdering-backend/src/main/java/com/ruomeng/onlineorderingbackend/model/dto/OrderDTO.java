package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单DTO
 */
@Data
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 餐台ID
     */
    private Long tableId;

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
     * 支付方式：1-在线支付，2-线下支付
     */
    private Integer paymentMethod;

    /**
     * 备注
     */
    private String remark;

    /**
     * 订单明细列表
     */
    private List<OrderDetailDTO> details;

    /**
     * 订单明细DTO
     */
    @Data
    public static class OrderDetailDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 明细ID
         */
        private Long id;

        /**
         * 菜品ID
         */
        private Long dishId;

        /**
         * 套餐ID
         */
        private Long setmealId;

        /**
         * 名称
         */
        private String name;

        /**
         * 图片
         */
        private String image;

        /**
         * 数量
         */
        private Integer quantity;

        /**
         * 单价
         */
        private BigDecimal price;

        /**
         * 口味
         */
        private String flavor;
    }
}
