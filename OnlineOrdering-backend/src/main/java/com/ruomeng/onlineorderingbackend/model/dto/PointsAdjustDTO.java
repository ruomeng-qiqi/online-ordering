package com.ruomeng.onlineorderingbackend.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 积分调整DTO
 */
@Data
public class PointsAdjustDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 调整类型：1-增加，2-减少
     */
    private Integer type;

    /**
     * 积分数量
     */
    private Integer points;

    /**
     * 备注
     */
    private String remark;
}
