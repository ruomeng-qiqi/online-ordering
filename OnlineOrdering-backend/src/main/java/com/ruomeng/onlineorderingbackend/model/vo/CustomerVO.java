package com.ruomeng.onlineorderingbackend.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 顾客VO
 */
@Data
public class CustomerVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 顾客ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 是否会员：0-否，1-是
     */
    private Integer isMember;

    /**
     * 当前积分
     */
    private Integer points;

    /**
     * 累计积分
     */
    private Integer totalPoints;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
