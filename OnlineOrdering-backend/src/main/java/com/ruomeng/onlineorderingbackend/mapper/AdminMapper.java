package com.ruomeng.onlineorderingbackend.mapper;

import com.ruomeng.onlineorderingbackend.model.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员 Mapper
 */
@Mapper
public interface AdminMapper {

    /**
     * 根据用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员信息
     */
    @Select("SELECT id, username, password FROM admin WHERE username = #{username}")
    Admin selectByUsername(String username);

    /**
     * 根据ID查询管理员
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    @Select("SELECT id, username, password, create_time, update_time FROM admin WHERE id = #{id}")
    Admin selectById(Long id);
}
