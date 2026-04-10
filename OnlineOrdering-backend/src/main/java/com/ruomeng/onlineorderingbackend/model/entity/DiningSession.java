package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 用餐会话
 */
@Data
public class DiningSession implements Serializable {

    /**
     * 会话ID
     */
    private Long id;

    /**
     * 桌号
     */
    private String tableNumber;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 微信用户OpenID
     */
    private String openId;

    /**
     * 扫码时间
     */
    private Date scanTime;

    /**
     * 会话关闭时间
     */
    private Date closeTime;

    /**
     * 状态：ACTIVE(活跃), CLOSED(已关闭)
     */
    private String status;

    private static final long serialVersionUID = 1L;
}
