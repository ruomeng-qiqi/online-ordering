package com.ruomeng.onlineorderingbackend.mapper;

import com.ruomeng.onlineorderingbackend.model.entity.OrderDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单明细Mapper
 */
@Mapper
public interface OrderDetailMapper {

    /**
     * 插入订单明细
     */
    @Insert("INSERT INTO order_detail (order_id, dish_id, setmeal_id, name, image, quantity, price, amount, flavor, create_time) " +
            "VALUES (#{orderId}, #{dishId}, #{setmealId}, #{name}, #{image}, #{quantity}, #{price}, #{amount}, #{flavor}, #{createTime})")
    int insert(OrderDetail orderDetail);

    /**
     * 根据订单ID查询订单明细
     */
    @Select("SELECT * FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> selectByOrderId(Long orderId);

    /**
     * 根据订单ID删除订单明细
     */
    @Delete("DELETE FROM order_detail WHERE order_id = #{orderId}")
    int deleteByOrderId(Long orderId);
}
