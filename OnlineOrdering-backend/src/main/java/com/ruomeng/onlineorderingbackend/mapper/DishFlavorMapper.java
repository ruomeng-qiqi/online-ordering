package com.ruomeng.onlineorderingbackend.mapper;

import com.ruomeng.onlineorderingbackend.model.entity.DishFlavor;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 菜品口味Mapper
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味
     */
    int insertBatch(@Param("flavors") List<DishFlavor> flavors);

    /**
     * 根据菜品ID删除口味
     */
    @Delete("DELETE FROM dish_flavor WHERE dish_id = #{dishId}")
    int deleteByDishId(Long dishId);

    /**
     * 根据菜品ID查询口味列表
     */
    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
    List<DishFlavor> selectByDishId(Long dishId);
}
