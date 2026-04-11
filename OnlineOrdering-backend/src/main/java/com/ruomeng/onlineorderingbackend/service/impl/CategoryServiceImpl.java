package com.ruomeng.onlineorderingbackend.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.CategoryMapper;
import com.ruomeng.onlineorderingbackend.mapper.DishMapper;
import com.ruomeng.onlineorderingbackend.mapper.SetmealMapper;
import com.ruomeng.onlineorderingbackend.model.dto.CategoryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.CategoryPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Category;
import com.ruomeng.onlineorderingbackend.model.vo.CategoryVO;
import com.ruomeng.onlineorderingbackend.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加分类
     */
    @Override
    public void add(CategoryDTO categoryDTO) {
        // 创建分类实体并复制属性
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(1); // 默认启用
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        // 插入数据库
        int result = categoryMapper.insert(category);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加分类失败");
        }
    }

    /**
     * 更新分类
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        // 校验分类ID
        if (categoryDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }

        // 查询分类是否存在
        Category category = categoryMapper.selectById(categoryDTO.getId());
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 更新分类信息
        category.setName(categoryDTO.getName());
        category.setSort(categoryDTO.getSort());
        category.setUpdateTime(LocalDateTime.now());
        
        int result = categoryMapper.update(category);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新分类失败");
        }
    }

    /**
     * 删除分类
     */
    @Override
    public void delete(Long id) {
        // 查询分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 检查是否有关联的菜品
        int dishCount = dishMapper.countByCategoryId(id);
        if (dishCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下有菜品，无法删除");
        }

        // 检查是否有关联的套餐
        int setmealCount = setmealMapper.countByCategoryId(id);
        if (setmealCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下有套餐，无法删除");
        }

        // 删除分类
        int result = categoryMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除分类失败");
        }
    }

    /**
     * 根据ID查询分类
     */
    @Override
    public CategoryVO getById(Long id) {
        // 查询分类
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 转换为VO
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }

    /**
     * 分页查询分类
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 开始分页
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        
        // 执行查询
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        
        // 获取总记录数和当前页数据
        long total = page.getTotal();
        List<Category> categories = page.getResult();
        
        // 转换为VO列表
        List<CategoryVO> records = categories.stream()
                .map(category -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());
        
        // 返回分页结果
        return new PageResult(total, records);
    }

    /**
     * 更新分类状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        // 查询分类是否存在
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 如果是禁用操作，需要检查是否有关联的菜品或套餐
        if (status == 0) {
            // 检查是否有关联的菜品
            int dishCount = dishMapper.countByCategoryId(id);
            if (dishCount > 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下有菜品，无法禁用");
            }

            // 检查是否有关联的套餐
            int setmealCount = setmealMapper.countByCategoryId(id);
            if (setmealCount > 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下有套餐，无法禁用");
            }
        }

        // 设置新状态和更新时间
        category.setId(id);
        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());
        
        // 更新状态
        int result = categoryMapper.updateStatus(category);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新状态失败");
        }
    }
}
