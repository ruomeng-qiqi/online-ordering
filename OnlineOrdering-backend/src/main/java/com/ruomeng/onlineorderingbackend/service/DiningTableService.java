package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.model.dto.DiningTableDTO;
import com.ruomeng.onlineorderingbackend.model.dto.DiningTablePageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.DiningTableVO;

import java.util.List;

/**
 * 餐台服务
 */
public interface DiningTableService {

    /**
     * 添加餐台
     */
    void add(DiningTableDTO diningTableDTO);

    /**
     * 更新餐台
     */
    void update(DiningTableDTO diningTableDTO);

    /**
     * 删除餐台
     */
    void delete(Long id);

    /**
     * 批量删除餐台
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询餐台
     */
    DiningTableVO getById(Long id);

    /**
     * 分页查询餐台
     */
    PageResult pageQuery(DiningTablePageQueryDTO diningTablePageQueryDTO);
}
