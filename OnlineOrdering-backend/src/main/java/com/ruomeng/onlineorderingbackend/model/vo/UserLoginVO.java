package com.ruomeng.onlineorderingbackend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录VO
 */
@Data
public class UserLoginVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * Token
     */
    private String token;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 是否会员：0-否，1-是
     */
    private Integer isMember;

    /**
     * 当前积分
     */
    private Integer points;

    /**
     * 账号状态：0-禁用，1-正常
     */
    private Integer status;
}
