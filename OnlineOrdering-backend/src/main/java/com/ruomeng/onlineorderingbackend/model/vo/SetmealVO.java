package com.ruomeng.onlineorderingbackend.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐VO
 */
@Data
public class SetmealVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID
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
     * 分类名称
     */
    private String category;

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
     * 状态：0-停售，1-起售
     */
    private Integer status;

    /**
     * 套餐包含的菜品列表
     */
    private List<SetmealDishVO> dishes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Data
    public static class SetmealDishVO {
        /**
         * 菜品ID
         */
        private Long dishId;

        /**
         * 菜品名称
         */
        private String dishName;

        /**
         * 份数
         */
        private Integer copies;

        /**
         * 价格
         */
        private BigDecimal price;
    }
}
