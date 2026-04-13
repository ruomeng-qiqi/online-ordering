package com.ruomeng.onlineorderingbackend.mapper;

import com.ruomeng.onlineorderingbackend.model.dto.CustomerPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Customer;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 顾客Mapper
 */
@Mapper
public interface CustomerMapper {

    /**
     * 分页查询顾客
     */
    List<Customer> pageQuery(CustomerPageQueryDTO customerPageQueryDTO);

    /**
     * 根据ID查询顾客
     */
    @Select("SELECT * FROM customer WHERE id = #{id}")
    Customer selectById(Long id);

    /**
     * 根据openid查询顾客
     */
    @Select("SELECT * FROM customer WHERE openid = #{openid}")
    Customer selectByOpenid(String openid);

    /**
     * 更新顾客状态
     */
    int updateStatus(Customer customer);

    /**
     * 更新顾客积分
     */
    int updatePoints(Customer customer);

    /**
     * 删除顾客
     */
    @Delete("DELETE FROM customer WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计顾客数量
     */
    @Select("SELECT COUNT(*) FROM customer WHERE status = 1")
    Integer countActiveCustomers();
}
