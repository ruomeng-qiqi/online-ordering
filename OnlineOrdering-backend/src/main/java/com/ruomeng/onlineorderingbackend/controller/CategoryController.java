package com.ruomeng.onlineorderingbackend.controller;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.CategoryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.CategoryPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.CategoryVO;
import com.ruomeng.onlineorderingbackend.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类接口
 */
@Api(tags = "分类接口")
@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     */
    @ApiOperation("添加分类")
    @PostMapping
    public Result<String> add(@RequestBody CategoryDTO categoryDTO) {
        log.info("添加分类：{}", categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success("添加成功");
    }

    /**
     * 更新分类
     */
    @ApiOperation("更新分类")
    @PutMapping
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("更新分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除分类
     */
    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除分类：{}", id);
        categoryService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询分类
     */
    @ApiOperation("根据ID查询分类")
    @GetMapping("/{id}")
    public Result<CategoryVO> getById(@PathVariable Long id) {
        log.info("查询分类：{}", id);
        CategoryVO categoryVO = categoryService.getById(id);
        return Result.success(categoryVO);
    }

    /**
     * 分页查询分类
     */
    @ApiOperation("分页查询分类")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 更新分类状态
     */
    @ApiOperation("更新分类状态")
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新分类状态：id={}, status={}", id, status);
        categoryService.updateStatus(id, status);
        return Result.success("更新成功");
    }
}
