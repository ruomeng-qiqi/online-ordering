package com.ruomeng.onlineorderingbackend.mapper;

import com.github.pagehelper.Page;
import com.ruomeng.onlineorderingbackend.model.dto.OrderPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Order;
import com.ruomeng.onlineorderingbackend.model.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper {

    /**
     * 分页查询订单列表
     */
    Page<OrderVO> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 根据ID查询订单
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order selectById(Long id);

    /**
     * 根据顾客ID统计订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE customer_id = #{customerId}")
    int countByCustomerId(Long customerId);

    /**
     * 更新订单
     */
    int update(Order order);

    /**
     * 删除订单
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 根据顾客ID查询订单列表
     */
    @Select("SELECT * FROM orders WHERE customer_id = #{customerId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<Order> listByCustomerId(@Param("customerId") Long customerId);

    /**
     * 查询餐台的待支付订单
     */
    @Select("SELECT * FROM orders WHERE table_id = #{tableId} AND order_status = 1 AND is_deleted = 0 ORDER BY create_time DESC LIMIT 1")
    Order selectPendingOrderByTableId(@Param("tableId") Long tableId);

    /**
     * 插入订单
     */
    int insert(Order order);

    /**
     * 软删除订单（用户端删除）
     */
    @Update("UPDATE orders SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int softDelete(@Param("id") Long id);

    /**
     * 取消订单
     */
    @Update("UPDATE orders SET order_status = 3, cancel_reason = #{cancelReason}, update_time = NOW() WHERE id = #{id}")
    int cancelOrder(@Param("id") Long id, @Param("cancelReason") String cancelReason);
}
