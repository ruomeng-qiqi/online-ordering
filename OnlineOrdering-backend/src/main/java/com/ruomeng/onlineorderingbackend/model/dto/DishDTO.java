package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 菜品DTO
 */
@Data
public class DishDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 菜品ID（编辑时需要）
     */
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 价格
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
     * 口味列表
     */
    private List<FlavorDTO> flavors;

    @Data
    public static class FlavorDTO {
        /**
         * 口味名称
         */
        private String name;

        /**
         * 口味选项
         */
        private List<String> options;
    }
}
