package com.ruomeng.onlineorderingbackend.controller;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.DiningTableDTO;
import com.ruomeng.onlineorderingbackend.model.dto.DiningTablePageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.DiningTableVO;
import com.ruomeng.onlineorderingbackend.service.DiningTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 餐台接口
 */
@Api(tags = "餐台接口")
@Slf4j
@RestController
@RequestMapping("/admin/table")
public class DiningTableController {

    @Autowired
    private DiningTableService diningTableService;

    /**
     * 添加餐台
     */
    @ApiOperation("添加餐台")
    @PostMapping
    public Result<String> add(@RequestBody DiningTableDTO diningTableDTO) {
        log.info("添加餐台：{}", diningTableDTO);
        diningTableService.add(diningTableDTO);
        return Result.success("添加成功");
    }

    /**
     * 更新餐台
     */
    @ApiOperation("更新餐台")
    @PutMapping
    public Result<String> update(@RequestBody DiningTableDTO diningTableDTO) {
        log.info("更新餐台：{}", diningTableDTO);
        diningTableService.update(diningTableDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除餐台
     */
    @ApiOperation("删除餐台")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除餐台：{}", id);
        diningTableService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除餐台
     */
    @ApiOperation("批量删除餐台")
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        log.info("批量删除餐台：{}", ids);
        diningTableService.deleteBatch(ids);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询餐台
     */
    @ApiOperation("根据ID查询餐台")
    @GetMapping("/{id}")
    public Result<DiningTableVO> getById(@PathVariable Long id) {
        log.info("查询餐台：{}", id);
        DiningTableVO diningTableVO = diningTableService.getById(id);
        return Result.success(diningTableVO);
    }

    /**
     * 分页查询餐台
     */
    @ApiOperation("分页查询餐台")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DiningTablePageQueryDTO diningTablePageQueryDTO) {
        log.info("分页查询餐台：{}", diningTablePageQueryDTO);
        PageResult pageResult = diningTableService.pageQuery(diningTablePageQueryDTO);
        return Result.success(pageResult);
    }
}
