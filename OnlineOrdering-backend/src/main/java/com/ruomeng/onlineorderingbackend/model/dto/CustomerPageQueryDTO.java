package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 顾客分页查询DTO
 */
@Data
public class CustomerPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 是否会员：0-否，1-是
     */
    private Integer isMember;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
}
