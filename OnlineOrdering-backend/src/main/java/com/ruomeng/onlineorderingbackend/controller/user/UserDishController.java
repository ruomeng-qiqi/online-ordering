package com.ruomeng.onlineorderingbackend.controller.user;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.vo.DishVO;
import com.ruomeng.onlineorderingbackend.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端菜品接口
 */
@Api(tags = "用户端-菜品接口")
@Slf4j
@RestController
@RequestMapping("/user/dish")
public class UserDishController {

    @Autowired
    private DishService dishService;

    /**
     * 根据分类查询菜品列表
     */
    @ApiOperation("根据分类查询菜品列表")
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam Long categoryId) {
        log.info("用户端根据分类查询菜品列表，categoryId={}", categoryId);
        List<DishVO> dishes = dishService.listByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 根据ID查询菜品
     */
    @ApiOperation("根据ID查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("用户端查询菜品详情，id={}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
}
