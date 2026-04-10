package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员登录请求
 */
@Data
public class AdminLoginDTO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    private static final long serialVersionUID = 1L;
}
