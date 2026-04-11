package com.ruomeng.onlineorderingbackend.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品VO
 */
@Data
public class DishVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 菜品ID
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
     * 分类名称
     */
    private String category;

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
     * 状态：0-停售，1-起售
     */
    private Integer status;

    /**
     * 口味列表
     */
    private List<FlavorVO> flavors;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Data
    public static class FlavorVO {
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
