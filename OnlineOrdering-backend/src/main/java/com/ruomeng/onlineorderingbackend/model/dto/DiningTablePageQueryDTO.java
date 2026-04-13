package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 餐台分页查询DTO
 */
@Data
public class DiningTablePageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 餐台号
     */
    private String tableNumber;

    /**
     * 餐台名称
     */
    private String tableName;

    /**
     * 状态：0-空闲，1-占用
     */
    private Integer status;

    /**
     * 页码（默认第1页）
     */
    private Integer page = 1;

    /**
     * 每页条数（默认10条）
     */
    private Integer pageSize = 10;
}
