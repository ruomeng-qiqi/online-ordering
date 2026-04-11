package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.model.dto.SetmealDTO;
import com.ruomeng.onlineorderingbackend.model.dto.SetmealPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.SetmealVO;
import java.util.List;

/**
 * 套餐服务接口
 */
public interface SetmealService {

    /**
     * 添加套餐
     */
    void add(SetmealDTO setmealDTO);

    /**
     * 更新套餐
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 删除套餐
     */
    void delete(Long id);

    /**
     * 批量删除套餐
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询套餐
     */
    SetmealVO getById(Long id);

    /**
     * 分页查询套餐
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 更新套餐状态
     */
    void updateStatus(Long id, Integer status);
}
