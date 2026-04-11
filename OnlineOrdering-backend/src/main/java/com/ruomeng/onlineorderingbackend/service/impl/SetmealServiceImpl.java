package com.ruomeng.onlineorderingbackend.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.CategoryMapper;
import com.ruomeng.onlineorderingbackend.mapper.DishMapper;
import com.ruomeng.onlineorderingbackend.mapper.SetmealDishMapper;
import com.ruomeng.onlineorderingbackend.mapper.SetmealMapper;
import com.ruomeng.onlineorderingbackend.model.dto.SetmealDTO;
import com.ruomeng.onlineorderingbackend.model.dto.SetmealPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Category;
import com.ruomeng.onlineorderingbackend.model.entity.Dish;
import com.ruomeng.onlineorderingbackend.model.entity.Setmeal;
import com.ruomeng.onlineorderingbackend.model.entity.SetmealDish;
import com.ruomeng.onlineorderingbackend.model.vo.SetmealVO;
import com.ruomeng.onlineorderingbackend.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐服务实现
 */
@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 添加套餐
     */
    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        // 创建套餐实体并复制属性
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(1); // 默认起售
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateTime(LocalDateTime.now());
        
        // 插入套餐
        int result = setmealMapper.insert(setmeal);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加套餐失败");
        }

        // 保存套餐菜品关系
        saveSetmealDishes(setmeal.getId(), setmealDTO.getDishes());
    }

    /**
     * 更新套餐
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 校验套餐ID
        if (setmealDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "套餐ID不能为空");
        }

        // 查询套餐是否存在
        Setmeal setmeal = setmealMapper.selectById(setmealDTO.getId());
        if (setmeal == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "套餐不存在");
        }

        // 更新套餐信息
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setUpdateTime(LocalDateTime.now());
        
        int result = setmealMapper.update(setmeal);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新套餐失败");
        }

        // 删除旧关系，保存新关系
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        saveSetmealDishes(setmeal.getId(), setmealDTO.getDishes());
    }

    /**
     * 删除套餐
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 查询套餐是否存在
        Setmeal setmeal = setmealMapper.selectById(id);
        if (setmeal == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "套餐不存在");
        }

        // 删除套餐菜品关系
        setmealDishMapper.deleteBySetmealId(id);
        
        // 删除套餐
        int result = setmealMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除套餐失败");
        }
    }

    /**
     * 批量删除套餐
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 校验参数
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "套餐ID列表不能为空");
        }

        // 删除所有套餐的菜品关系
        for (Long id : ids) {
            setmealDishMapper.deleteBySetmealId(id);
        }

        // 批量删除套餐
        int result = setmealMapper.deleteBatchByIds(ids);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量删除套餐失败");
        }
    }

    /**
     * 根据ID查询套餐
     */
    @Override
    public SetmealVO getById(Long id) {
        // 查询套餐
        Setmeal setmeal = setmealMapper.selectById(id);
        if (setmeal == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "套餐不存在");
        }

        // 转换为VO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        // 查询分类名称
        Category category = categoryMapper.selectById(setmeal.getCategoryId());
        if (category != null) {
            setmealVO.setCategory(category.getName());
        }

        // 查询套餐菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        setmealVO.setDishes(convertSetmealDishesToVO(setmealDishes));

        return setmealVO;
    }

    /**
     * 分页查询套餐
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 开始分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        
        // 执行查询
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        
        // 获取总记录数和当前页数据
        long total = page.getTotal();
        List<Setmeal> setmeals = page.getResult();
        
        // 转换为VO列表
        List<SetmealVO> records = setmeals.stream()
                .map(setmeal -> {
                    SetmealVO vo = new SetmealVO();
                    BeanUtils.copyProperties(setmeal, vo);
                    
                    // 查询分类名称
                    Category category = categoryMapper.selectById(setmeal.getCategoryId());
                    if (category != null) {
                        vo.setCategory(category.getName());
                    }
                    
                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());
        
        // 返回分页结果
        return new PageResult(total, records);
    }

    /**
     * 更新套餐状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        // 查询套餐是否存在
        Setmeal setmeal = setmealMapper.selectById(id);
        if (setmeal == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "套餐不存在");
        }

        // 设置新状态和更新时间
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmeal.setUpdateTime(LocalDateTime.now());
        
        // 更新状态
        int result = setmealMapper.updateStatus(setmeal);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新状态失败");
        }
    }

    /**
     * 保存套餐菜品关系
     */
    private void saveSetmealDishes(Long setmealId, List<SetmealDTO.SetmealDishDTO> dishDTOs) {
        if (dishDTOs == null || dishDTOs.isEmpty()) {
            return;
        }

        // 转换为实体列表
        List<SetmealDish> setmealDishes = new ArrayList<>();
        for (SetmealDTO.SetmealDishDTO dishDTO : dishDTOs) {
            SetmealDish setmealDish = new SetmealDish();
            setmealDish.setSetmealId(setmealId);
            setmealDish.setDishId(dishDTO.getDishId());
            setmealDish.setCopies(dishDTO.getCopies());
            
            // 设置时间
            setmealDish.setCreateTime(LocalDateTime.now());
            setmealDish.setUpdateTime(LocalDateTime.now());
            
            setmealDishes.add(setmealDish);
        }

        // 批量插入
        if (!setmealDishes.isEmpty()) {
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 转换套餐菜品为VO
     */
    private List<SetmealVO.SetmealDishVO> convertSetmealDishesToVO(List<SetmealDish> setmealDishes) {
        if (setmealDishes == null || setmealDishes.isEmpty()) {
            return new ArrayList<>();
        }

        return setmealDishes.stream().map(setmealDish -> {
            SetmealVO.SetmealDishVO dishVO = new SetmealVO.SetmealDishVO();
            dishVO.setDishId(setmealDish.getDishId());
            dishVO.setCopies(setmealDish.getCopies());

            // 查询菜品信息
            Dish dish = dishMapper.selectById(setmealDish.getDishId());
            if (dish != null) {
                dishVO.setDishName(dish.getName());
                dishVO.setPrice(dish.getPrice());
            }

            return dishVO;
        }).collect(Collectors.toList());
    }
}
