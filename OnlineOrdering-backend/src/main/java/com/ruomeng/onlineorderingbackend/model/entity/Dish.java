package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 菜品信息
 */
@Data
public class Dish implements Serializable {

    /**
     * 菜品ID
     */
    private Long id;

    /**
     * 菜品名称
     */
    private String dishName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 价格，单位：元
     */
    private BigDecimal price;

    /**
     * 菜品图片URL
     */
    private String imageUrl;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 状态：AVAILABLE(可用), UNAVAILABLE(不可用), DELETED(已删除)
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
