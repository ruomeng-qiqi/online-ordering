package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.model.dto.CustomerPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.PointsAdjustDTO;
import com.ruomeng.onlineorderingbackend.model.vo.CustomerVO;
import com.ruomeng.onlineorderingbackend.model.vo.PointsRecordVO;

import java.util.List;

/**
 * 顾客服务
 */
public interface CustomerService {

    /**
     * 分页查询顾客
     */
    PageResult pageQuery(CustomerPageQueryDTO customerPageQueryDTO);

    /**
     * 根据ID查询顾客详情
     */
    CustomerVO getById(Long id);

    /**
     * 更新顾客状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除顾客
     */
    void deleteById(Long id);

    /**
     * 调整顾客积分
     */
    void adjustPoints(PointsAdjustDTO pointsAdjustDTO);

    /**
     * 查询顾客积分记录
     */
    List<PointsRecordVO> getPointsRecords(Long customerId);
}
