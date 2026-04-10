package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理员
 */
@Data
public class Admin implements Serializable {

    /**
     * 管理员ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

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
