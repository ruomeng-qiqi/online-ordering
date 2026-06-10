package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.model.dto.OrderCancelDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.PaymentDTO;
import com.ruomeng.onlineorderingbackend.model.vo.OrderVO;

import java.util.List;

/**
 * 订单服务
 */
public interface OrderService {

    /**
     * 分页查询订单列表
     */
    PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 查询订单详情
     */
    OrderVO getById(Long id);

    /**
     * 更新订单
     */
    void update(OrderDTO orderDTO);

    /**
     * 完成订单
     */
    void complete(Long id);

    /**
     * 取消订单
     */
    void cancel(OrderCancelDTO orderCancelDTO);

    /**
     * 删除订单
     */
    void delete(Long id);

    /**
     * 根据顾客ID查询订单列表
     */
    List<OrderVO> listByCustomerId(Long customerId);

    /**
     * 根据ID查询订单详情（含明细）
     */
    OrderVO getByIdWithDetails(Long id);

    /**
     * 创建订单（用户端）
     */
    OrderVO createOrder(OrderDTO orderDTO);

    /**
     * 查询餐台的待支付订单
     */
    OrderVO getPendingOrderByTableId(Long tableId);

    /**
     * 删除订单（用户端软删除）
     */
    void deleteOrderByUser(Long id);

    /**
     * 支付订单
     */
    void payOrder(PaymentDTO paymentDTO);
}
