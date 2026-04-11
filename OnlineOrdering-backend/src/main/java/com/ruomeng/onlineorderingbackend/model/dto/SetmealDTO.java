package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 套餐DTO
 */
@Data
public class SetmealDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID（编辑时需要）
     */
    private Long id;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 套餐价格
     */
    private BigDecimal price;

    /**
     * 图片路径
     */
    private String image;

    /**
     * 描述
     */
    private String description;

    /**
     * 套餐包含的菜品列表
     */
    private List<SetmealDishDTO> dishes;

    @Data
    public static class SetmealDishDTO {
        /**
         * 菜品ID
         */
        private Long dishId;

        /**
         * 份数
         */
        private Integer copies;
    }
}
