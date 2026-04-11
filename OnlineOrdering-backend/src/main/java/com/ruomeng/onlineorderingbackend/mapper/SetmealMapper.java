package com.ruomeng.onlineorderingbackend.mapper;

import com.github.pagehelper.Page;
import com.ruomeng.onlineorderingbackend.model.dto.SetmealPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Setmeal;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 套餐Mapper
 */
@Mapper
public interface SetmealMapper {

    /**
     * 插入套餐
     */
    int insert(Setmeal setmeal);

    /**
     * 更新套餐
     */
    int update(Setmeal setmeal);

    /**
     * 根据ID删除套餐
     */
    @Delete("DELETE FROM setmeal WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 批量删除套餐
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);

    /**
     * 根据ID查询套餐
     */
    @Select("SELECT * FROM setmeal WHERE id = #{id}")
    Setmeal selectById(Long id);

    /**
     * 分页查询套餐
     */
    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 更新套餐状态
     */
    int updateStatus(Setmeal setmeal);

    /**
     * 根据分类ID查询套餐数量
     */
    @Select("SELECT COUNT(*) FROM setmeal WHERE category_id = #{categoryId}")
    int countByCategoryId(Long categoryId);
}
