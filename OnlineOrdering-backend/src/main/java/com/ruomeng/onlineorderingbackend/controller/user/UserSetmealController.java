package com.ruomeng.onlineorderingbackend.controller.user;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.vo.SetmealVO;
import com.ruomeng.onlineorderingbackend.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端套餐接口
 */
@Api(tags = "用户端-套餐接口")
@Slf4j
@RestController
@RequestMapping("/user/setmeal")
public class UserSetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类ID查询套餐列表（只返回起售状态的套餐）
     */
    @ApiOperation("根据分类ID查询套餐列表")
    @GetMapping("/list")
    public Result<List<SetmealVO>> listByCategoryId(@RequestParam Long categoryId) {
        log.info("用户端查询套餐列表，分类ID：{}", categoryId);
        List<SetmealVO> list = setmealService.listByCategoryIdAndStatus(categoryId, 1);
        return Result.success(list);
    }

    /**
     * 根据ID查询套餐详情
     */
    @ApiOperation("根据ID查询套餐详情")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("用户端查询套餐详情，ID：{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        
        // 检查套餐状态，只返回起售状态的套餐
        if (setmealVO.getStatus() != 1) {
            return Result.error("该套餐已停售");
        }
        
        return Result.success(setmealVO);
    }
}
