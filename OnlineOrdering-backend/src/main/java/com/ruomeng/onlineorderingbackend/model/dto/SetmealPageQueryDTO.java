package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 套餐分页查询DTO
 */
@Data
public class SetmealPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 状态：0-停售，1-起售
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
