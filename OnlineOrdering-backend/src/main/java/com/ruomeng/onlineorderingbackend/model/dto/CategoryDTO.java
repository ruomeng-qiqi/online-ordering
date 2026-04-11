package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 分类DTO
 */
@Data
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID（编辑时需要）
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类类型：1-菜品分类，2-套餐分类
     */
    private Integer type;

    /**
     * 排序
     */
    private Integer sort;
}
