package com.ruomeng.onlineorderingbackend.mapper;

import com.ruomeng.onlineorderingbackend.model.entity.PointsRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 积分记录Mapper
 */
@Mapper
public interface PointsRecordMapper {

    /**
     * 插入积分记录
     */
    void insert(PointsRecord pointsRecord);

    /**
     * 根据顾客ID查询积分记录
     */
    @Select("SELECT * FROM points_record WHERE customer_id = #{customerId} ORDER BY create_time DESC")
    List<PointsRecord> selectByCustomerId(Long customerId);

    /**
     * 根据顾客ID删除积分记录
     */
    @Delete("DELETE FROM points_record WHERE customer_id = #{customerId}")
    void deleteByCustomerId(Long customerId);
}
