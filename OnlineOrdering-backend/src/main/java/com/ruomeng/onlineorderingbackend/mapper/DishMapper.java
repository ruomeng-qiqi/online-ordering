package com.ruomeng.onlineorderingbackend.mapper;

import com.github.pagehelper.Page;
import com.ruomeng.onlineorderingbackend.model.dto.DishPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Dish;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 菜品Mapper
 */
@Mapper
public interface DishMapper {

    /**
     * 插入菜品
     */
    int insert(Dish dish);

    /**
     * 更新菜品
     */
    int update(Dish dish);

    /**
     * 根据ID删除菜品
     */
    @Delete("DELETE FROM dish WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 批量删除菜品
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);

    /**
     * 根据ID查询菜品
     */
    @Select("SELECT * FROM dish WHERE id = #{id}")
    Dish selectById(Long id);

    /**
     * 分页查询菜品
     */
    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 更新菜品状态
     */
    int updateStatus(Dish dish);

    /**
     * 根据分类ID查询菜品数量
     */
    @Select("SELECT COUNT(*) FROM dish WHERE category_id = #{categoryId}")
    int countByCategoryId(Long categoryId);
}
