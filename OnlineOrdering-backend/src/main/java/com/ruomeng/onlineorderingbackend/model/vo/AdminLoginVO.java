package com.ruomeng.onlineorderingbackend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员登录响应
 */
@Data
public class AdminLoginVO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * JWT Token
     */
    private String token;

    private static final long serialVersionUID = 1L;
}
