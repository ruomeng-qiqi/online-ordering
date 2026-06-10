package com.ruomeng.onlineorderingbackend.controller.user;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.vo.DiningTableVO;
import com.ruomeng.onlineorderingbackend.service.DiningTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端餐台接口
 */
@Api(tags = "用户端餐台接口")
@Slf4j
@RestController
@RequestMapping("/user/table")
public class UserTableController {

    @Autowired
    private DiningTableService diningTableService;

    /**
     * 根据餐台号查询餐台信息
     */
    @ApiOperation("根据餐台号查询餐台信息")
    @GetMapping("/number")
    public Result<DiningTableVO> getByTableNumber(@RequestParam String tableNumber) {
        log.info("根据餐台号查询餐台信息：{}", tableNumber);
        DiningTableVO diningTableVO = diningTableService.getByTableNumber(tableNumber);
        return Result.success(diningTableVO);
    }
}
