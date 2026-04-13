package com.ruomeng.onlineorderingbackend.mapper;

import com.github.pagehelper.Page;
import com.ruomeng.onlineorderingbackend.model.dto.DiningTablePageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.DiningTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 餐台Mapper
 */
@Mapper
public interface DiningTableMapper {

    /**
     * 插入餐台
     */
    int insert(DiningTable diningTable);

    /**
     * 更新餐台
     */
    int update(DiningTable diningTable);

    /**
     * 根据ID删除餐台
     */
    @Delete("DELETE FROM dining_table WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 批量删除餐台
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);

    /**
     * 根据ID查询餐台
     */
    @Select("SELECT * FROM dining_table WHERE id = #{id}")
    DiningTable selectById(Long id);

    /**
     * 根据餐台号查询餐台
     */
    @Select("SELECT * FROM dining_table WHERE table_number = #{tableNumber}")
    DiningTable selectByTableNumber(String tableNumber);

    /**
     * 分页查询餐台
     */
    Page<DiningTable> pageQuery(DiningTablePageQueryDTO diningTablePageQueryDTO);

    /**
     * 更新餐台状态
     */
    @Update("UPDATE dining_table SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateStatus(DiningTable diningTable);
}
