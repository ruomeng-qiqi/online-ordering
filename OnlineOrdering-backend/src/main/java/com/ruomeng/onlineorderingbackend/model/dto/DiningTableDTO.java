package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 餐台DTO
 */
@Data
public class DiningTableDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 餐台ID（更新时需要）
     */
    private Long id;

    /**
     * 餐台号
     */
    private String tableNumber;

    /**
     * 餐台名称
     */
    private String tableName;

    /**
     * 座位数
     */
    private Integer seats;

    /**
     * 排序
     */
    private Integer sort;
}
