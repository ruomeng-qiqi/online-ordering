package com.ruomeng.onlineorderingbackend.controller;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.SetmealDTO;
import com.ruomeng.onlineorderingbackend.model.dto.SetmealPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.SetmealVO;
import com.ruomeng.onlineorderingbackend.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 套餐接口
 */
@Api(tags = "套餐接口")
@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐
     */
    @ApiOperation("添加套餐")
    @PostMapping
    public Result<String> add(@RequestBody SetmealDTO setmealDTO) {
        log.info("添加套餐：{}", setmealDTO);
        setmealService.add(setmealDTO);
        return Result.success("添加成功");
    }

    /**
     * 更新套餐
     */
    @ApiOperation("更新套餐")
    @PutMapping
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("更新套餐：{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除套餐
     */
    @ApiOperation("删除套餐")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除套餐：{}", id);
        setmealService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除套餐
     */
    @ApiOperation("批量删除套餐")
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        log.info("批量删除套餐：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询套餐
     */
    @ApiOperation("根据ID查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("查询套餐：{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 分页查询套餐
     */
    @ApiOperation("分页查询套餐")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 更新套餐状态
     */
    @ApiOperation("更新套餐状态")
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新套餐状态：id={}, status={}", id, status);
        setmealService.updateStatus(id, status);
        return Result.success("更新成功");
    }
}
