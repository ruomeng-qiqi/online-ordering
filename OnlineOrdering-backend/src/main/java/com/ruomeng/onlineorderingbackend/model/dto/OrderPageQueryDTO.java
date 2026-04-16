package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 订单分页查询DTO
 */
@Data
public class OrderPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 顾客昵称
     */
    private String customerNickname;

    /**
     * 餐台号
     */
    private String tableNumber;

    /**
     * 订单状态：1-待支付，2-已完成，3-已取消
     */
    private Integer orderStatus;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
