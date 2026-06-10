package com.ruomeng.onlineorderingbackend.controller.user;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.entity.Category;
import com.ruomeng.onlineorderingbackend.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端分类接口
 */
@Api(tags = "用户端-分类接口")
@Slf4j
@RestController
@RequestMapping("/user/category")
public class UserCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类列表
     */
    @ApiOperation("查询分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        log.info("用户端查询分类列表");
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }
}
