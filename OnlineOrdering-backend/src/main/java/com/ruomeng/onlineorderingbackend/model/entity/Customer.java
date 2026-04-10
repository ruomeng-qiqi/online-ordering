package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 顾客信息
 */
@Data
public class Customer implements Serializable {

    /**
     * 顾客ID
     */
    private Long id;

    /**
     * 微信用户OpenID
     */
    private String openId;

    /**
     * 微信昵称
     */
    private String nickname;

    /**
     * 微信头像URL
     */
    private String avatarUrl;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 积分余额
     */
    private Integer points;

    /**
     * 是否会员：0(非会员), 1(会员)
     */
    private Integer isMember;

    /**
     * 成为会员时间
     */
    private Date memberSince;

    /**
     * 会员到期时间
     */
    private Date memberExpireTime;

    /**
     * 累计消费金额
     */
    private BigDecimal totalSpent;

    /**
     * 累计订单数
     */
    private Integer totalOrders;

    /**
     * 状态：ACTIVE(活跃), INACTIVE(不活跃), BLOCKED(已封禁)
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
