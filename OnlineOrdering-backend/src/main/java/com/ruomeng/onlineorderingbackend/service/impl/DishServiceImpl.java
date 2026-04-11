package com.ruomeng.onlineorderingbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.CategoryMapper;
import com.ruomeng.onlineorderingbackend.mapper.DishFlavorMapper;
import com.ruomeng.onlineorderingbackend.mapper.DishMapper;
import com.ruomeng.onlineorderingbackend.mapper.SetmealDishMapper;
import com.ruomeng.onlineorderingbackend.model.dto.DishDTO;
import com.ruomeng.onlineorderingbackend.model.dto.DishPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Category;
import com.ruomeng.onlineorderingbackend.model.entity.Dish;
import com.ruomeng.onlineorderingbackend.model.entity.DishFlavor;
import com.ruomeng.onlineorderingbackend.model.vo.DishVO;
import com.ruomeng.onlineorderingbackend.service.DishService;
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
 * 菜品服务实现
 */
@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 添加菜品
     */
    @Override
    @Transactional
    public void add(DishDTO dishDTO) {
        // 创建菜品实体并复制属性
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(1); // 默认起售
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        
        // 插入菜品
        int result = dishMapper.insert(dish);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加菜品失败");
        }

        // 保存口味
        saveFlavors(dish.getId(), dishDTO.getFlavors());
    }

    /**
     * 更新菜品
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        // 校验菜品ID
        if (dishDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "菜品ID不能为空");
        }

        // 查询菜品是否存在
        Dish dish = dishMapper.selectById(dishDTO.getId());
        if (dish == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "菜品不存在");
        }

        // 更新菜品信息
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setUpdateTime(LocalDateTime.now());
        
        int result = dishMapper.update(dish);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新菜品失败");
        }

        // 删除旧口味，保存新口味
        dishFlavorMapper.deleteByDishId(dish.getId());
        saveFlavors(dish.getId(), dishDTO.getFlavors());
    }

    /**
     * 删除菜品
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 查询菜品是否存在
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "菜品不存在");
        }

        // 检查是否被套餐关联
        int count = setmealDishMapper.countByDishId(id);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该菜品已被套餐关联，无法删除");
        }

        // 删除菜品口味
        dishFlavorMapper.deleteByDishId(id);
        
        // 删除菜品
        int result = dishMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除菜品失败");
        }
    }

    /**
     * 批量删除菜品
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 校验参数
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "菜品ID列表不能为空");
        }

        // 检查是否被套餐关联
        for (Long id : ids) {
            int count = setmealDishMapper.countByDishId(id);
            if (count > 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "部分菜品已被套餐关联，无法删除");
            }
        }

        // 删除所有菜品的口味
        for (Long id : ids) {
            dishFlavorMapper.deleteByDishId(id);
        }

        // 批量删除菜品
        int result = dishMapper.deleteBatchByIds(ids);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量删除菜品失败");
        }
    }

    /**
     * 根据ID查询菜品
     */
    @Override
    public DishVO getById(Long id) {
        // 查询菜品
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "菜品不存在");
        }

        // 转换为VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        // 查询分类名称
        Category category = categoryMapper.selectById(dish.getCategoryId());
        if (category != null) {
            dishVO.setCategory(category.getName());
        }

        // 查询口味
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        dishVO.setFlavors(convertFlavorsToVO(flavors));

        return dishVO;
    }

    /**
     * 分页查询菜品
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 开始分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        
        // 执行查询
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);
        
        // 获取总记录数和当前页数据
        long total = page.getTotal();
        List<Dish> dishes = page.getResult();
        
        // 转换为VO列表
        List<DishVO> records = dishes.stream()
                .map(dish -> {
                    DishVO vo = new DishVO();
                    BeanUtils.copyProperties(dish, vo);
                    
                    // 查询分类名称
                    Category category = categoryMapper.selectById(dish.getCategoryId());
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
     * 更新菜品状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        // 查询菜品是否存在
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "菜品不存在");
        }

        // 如果是停售操作，需要检查是否被套餐关联
        if (status == 0) {
            int count = setmealDishMapper.countByDishId(id);
            if (count > 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该菜品已被套餐关联，无法停售");
            }
        }

        // 设置新状态和更新时间
        dish.setId(id);
        dish.setStatus(status);
        dish.setUpdateTime(LocalDateTime.now());
        
        // 更新状态
        int result = dishMapper.updateStatus(dish);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新状态失败");
        }
    }

    /**
     * 保存口味
     */
    private void saveFlavors(Long dishId, List<DishDTO.FlavorDTO> flavorDTOs) {
        if (flavorDTOs == null || flavorDTOs.isEmpty()) {
            return;
        }

        // 转换为实体列表
        List<DishFlavor> flavors = new ArrayList<>();
        for (DishDTO.FlavorDTO flavorDTO : flavorDTOs) {
            DishFlavor flavor = new DishFlavor();
            flavor.setDishId(dishId);
            flavor.setName(flavorDTO.getName());
            
            // 将口味选项转换为JSON字符串
            try {
                flavor.setValue(objectMapper.writeValueAsString(flavorDTO.getOptions()));
            } catch (JsonProcessingException e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "口味数据格式错误");
            }
            
            // 设置时间
            flavor.setCreateTime(LocalDateTime.now());
            flavor.setUpdateTime(LocalDateTime.now());
            
            flavors.add(flavor);
        }

        // 批量插入
        if (!flavors.isEmpty()) {
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 转换口味为VO
     */
    private List<DishVO.FlavorVO> convertFlavorsToVO(List<DishFlavor> flavors) {
        if (flavors == null || flavors.isEmpty()) {
            return new ArrayList<>();
        }

        return flavors.stream().map(flavor -> {
            DishVO.FlavorVO flavorVO = new DishVO.FlavorVO();
            flavorVO.setName(flavor.getName());
            
            // 将JSON字符串转换为列表
            try {
                List<String> options = objectMapper.readValue(flavor.getValue(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                flavorVO.setOptions(options);
            } catch (JsonProcessingException e) {
                log.error("解析口味数据失败", e);
                flavorVO.setOptions(new ArrayList<>());
            }
            return flavorVO;
        }).collect(Collectors.toList());
    }
}
