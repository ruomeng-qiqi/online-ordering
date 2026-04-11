package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜品口味实体
 */
@Data
public class DishFlavor implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 口味ID
     */
    private Long id;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 口味名称
     */
    private String name;

    /**
     * 口味选项（JSON数组格式）
     */
    private String value;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
