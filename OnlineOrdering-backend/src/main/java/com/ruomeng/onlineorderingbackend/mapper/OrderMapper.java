package com.ruomeng.onlineorderingbackend.mapper;

import com.github.pagehelper.Page;
import com.ruomeng.onlineorderingbackend.model.dto.OrderPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Order;
import com.ruomeng.onlineorderingbackend.model.vo.OrderVO;
import org.apache.ibatis.annotations.*;

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
}
