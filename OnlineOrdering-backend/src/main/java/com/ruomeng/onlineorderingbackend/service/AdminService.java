package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.model.dto.AdminLoginDTO;
import com.ruomeng.onlineorderingbackend.model.vo.AdminLoginVO;

/**
 * 管理员服务
 */
public interface AdminService {

    /**
     * 管理员登录
     *
     * @param loginDTO 登录请求
     * @return 登录响应
     */
    AdminLoginVO login(AdminLoginDTO loginDTO);
}
