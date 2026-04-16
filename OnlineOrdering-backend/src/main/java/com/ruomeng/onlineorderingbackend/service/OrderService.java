package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.model.dto.OrderCancelDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.OrderDetailVO;
import com.ruomeng.onlineorderingbackend.model.vo.OrderVO;

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
    OrderDetailVO getById(Long id);

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
}
