package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.model.dto.DishDTO;
import com.ruomeng.onlineorderingbackend.model.dto.DishPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.DishVO;
import java.util.List;

/**
 * 菜品服务接口
 */
public interface DishService {

    /**
     * 添加菜品
     */
    void add(DishDTO dishDTO);

    /**
     * 更新菜品
     */
    void update(DishDTO dishDTO);

    /**
     * 删除菜品
     */
    void delete(Long id);

    /**
     * 批量删除菜品
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询菜品
     */
    DishVO getById(Long id);

    /**
     * 分页查询菜品
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 更新菜品状态
     */
    void updateStatus(Long id, Integer status);
}
