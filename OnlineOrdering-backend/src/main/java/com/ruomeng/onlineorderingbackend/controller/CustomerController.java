package com.ruomeng.onlineorderingbackend.controller;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.CustomerPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.PointsAdjustDTO;
import com.ruomeng.onlineorderingbackend.model.vo.CustomerVO;
import com.ruomeng.onlineorderingbackend.model.vo.PointsRecordVO;
import com.ruomeng.onlineorderingbackend.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 顾客管理接口
 */
@Api(tags = "顾客管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 分页查询顾客
     */
    @ApiOperation("分页查询顾客")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CustomerPageQueryDTO customerPageQueryDTO) {
        log.info("分页查询顾客：{}", customerPageQueryDTO);
        PageResult pageResult = customerService.pageQuery(customerPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询顾客详情
     */
    @ApiOperation("根据ID查询顾客详情")
    @GetMapping("/{id}")
    public Result<CustomerVO> getById(@PathVariable Long id) {
        log.info("查询顾客详情：{}", id);
        CustomerVO customerVO = customerService.getById(id);
        return Result.success(customerVO);
    }

    /**
     * 更新顾客状态
     */
    @ApiOperation("更新顾客状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新顾客状态：id={}, status={}", id, status);
        customerService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 删除顾客
     */
    @ApiOperation("删除顾客")
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        log.info("删除顾客：{}", id);
        customerService.deleteById(id);
        return Result.success();
    }

    /**
     * 调整顾客积分
     */
    @ApiOperation("调整顾客积分")
    @PostMapping("/points/adjust")
    public Result<Void> adjustPoints(@RequestBody PointsAdjustDTO pointsAdjustDTO) {
        log.info("调整顾客积分：{}", pointsAdjustDTO);
        customerService.adjustPoints(pointsAdjustDTO);
        return Result.success();
    }

    /**
     * 查询顾客积分记录
     */
    @ApiOperation("查询顾客积分记录")
    @GetMapping("/{customerId}/points/records")
    public Result<List<PointsRecordVO>> getPointsRecords(@PathVariable Long customerId) {
        log.info("查询顾客积分记录：{}", customerId);
        List<PointsRecordVO> pointsRecords = customerService.getPointsRecords(customerId);
        return Result.success(pointsRecords);
    }
}
