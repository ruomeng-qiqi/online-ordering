package com.ruomeng.onlineorderingbackend.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录VO
 */
@Data
public class PointsRecordVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 积分记录ID
     */
    private Long id;

    /**
     * 类型：1-订单获得，2-积分抵扣，3-手动调整
     */
    private Integer type;

    /**
     * 积分变动（正数增加，负数减少）
     */
    private Integer points;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
