package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐实体
 */
@Data
public class Setmeal implements Serializable {
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
