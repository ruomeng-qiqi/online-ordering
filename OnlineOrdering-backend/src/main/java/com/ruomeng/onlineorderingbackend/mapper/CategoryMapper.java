package com.ruomeng.onlineorderingbackend.mapper;

import com.github.pagehelper.Page;
import com.ruomeng.onlineorderingbackend.model.dto.CategoryPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Category;
import org.apache.ibatis.annotations.*;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper {

    /**
     * 插入分类
     */
    int insert(Category category);

    /**
     * 更新分类
     */
    int update(Category category);

    /**
     * 根据ID删除分类
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 根据ID查询分类
     */
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category selectById(Long id);

    /**
     * 分页查询分类
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 更新分类状态
     */
    int updateStatus(Category category);
}
