package com.ruomeng.onlineorderingbackend.controller;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.DishDTO;
import com.ruomeng.onlineorderingbackend.model.dto.DishPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.DishVO;
import com.ruomeng.onlineorderingbackend.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 菜品接口
 */
@Api(tags = "菜品接口")
@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     */
    @ApiOperation("添加菜品")
    @PostMapping
    public Result<String> add(@RequestBody DishDTO dishDTO) {
        log.info("添加菜品：{}", dishDTO);
        dishService.add(dishDTO);
        return Result.success("添加成功");
    }

    /**
     * 更新菜品
     */
    @ApiOperation("更新菜品")
    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        log.info("更新菜品：{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除菜品
     */
    @ApiOperation("删除菜品")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除菜品：{}", id);
        dishService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除菜品
     */
    @ApiOperation("批量删除菜品")
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        log.info("批量删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询菜品
     */
    @ApiOperation("根据ID查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 分页查询菜品
     */
    @ApiOperation("分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 更新菜品状态
     */
    @ApiOperation("更新菜品状态")
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新菜品状态：id={}, status={}", id, status);
        dishService.updateStatus(id, status);
        return Result.success("更新成功");
    }
}
