package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.model.dto.CategoryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.CategoryPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.CategoryVO;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 添加分类
     */
    void add(CategoryDTO categoryDTO);

    /**
     * 更新分类
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 删除分类
     */
    void delete(Long id);

    /**
     * 根据ID查询分类
     */
    CategoryVO getById(Long id);

    /**
     * 分页查询分类
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 更新分类状态
     */
    void updateStatus(Long id, Integer status);
}
