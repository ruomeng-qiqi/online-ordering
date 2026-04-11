package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 分类分页查询DTO
 */
@Data
public class CategoryPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类类型：1-菜品分类，2-套餐分类
     */
    private Integer type;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
