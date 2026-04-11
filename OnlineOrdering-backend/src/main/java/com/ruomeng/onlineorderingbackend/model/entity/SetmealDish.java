package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 套餐菜品关系实体
 */
@Data
public class SetmealDish implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    private Long id;

    /**
     * 套餐ID
     */
    private Long setmealId;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 份数
     */
    private Integer copies;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
