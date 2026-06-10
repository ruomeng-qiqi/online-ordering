package com.ruomeng.onlineorderingbackend.mapper;

import com.ruomeng.onlineorderingbackend.model.entity.Customer;
import org.apache.ibatis.annotations.*;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     */
    @Select("SELECT * FROM customer WHERE openid = #{openid}")
    Customer selectByOpenid(String openid);

    /**
     * 插入用户
     */
    @Insert("INSERT INTO customer (openid, nickname, avatar, gender, is_member, points, total_points, status, create_time, update_time) " +
            "VALUES (#{openid}, #{nickname}, #{avatar}, #{gender}, #{isMember}, #{points}, #{totalPoints}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);

    /**
     * 更新用户信息
     */
    int update(Customer customer);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM customer WHERE id = #{id}")
    Customer selectById(Long id);
}
