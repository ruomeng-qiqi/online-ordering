package com.ruomeng.onlineorderingbackend.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 餐台实体
 */
@Data
public class DiningTable implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 餐台ID
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
     * 状态：0-空闲，1-占用
     */
    private Integer status;

    /**
     * 二维码URL
     */
    private String qrCode;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
