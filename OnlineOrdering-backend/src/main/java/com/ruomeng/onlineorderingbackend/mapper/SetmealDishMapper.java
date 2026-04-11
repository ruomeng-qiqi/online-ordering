package com.ruomeng.onlineorderingbackend.mapper;

import com.ruomeng.onlineorderingbackend.model.entity.SetmealDish;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 套餐菜品关系Mapper
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 批量插入套餐菜品关系
     */
    int insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /**
     * 根据套餐ID删除关系
     */
    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    int deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐ID查询菜品列表
     */
    @Select("SELECT sd.*, d.name as dish_name, d.price FROM setmeal_dish sd " +
            "LEFT JOIN dish d ON sd.dish_id = d.id " +
            "WHERE sd.setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);

    /**
     * 根据菜品ID查询关联的套餐数量
     */
    @Select("SELECT COUNT(*) FROM setmeal_dish WHERE dish_id = #{dishId}")
    int countByDishId(Long dishId);
}
