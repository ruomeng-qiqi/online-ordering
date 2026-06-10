package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 微信登录凭证
     */
    private String code;
}
