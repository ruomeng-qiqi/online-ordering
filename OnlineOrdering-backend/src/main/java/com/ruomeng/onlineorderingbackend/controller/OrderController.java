package com.ruomeng.onlineorderingbackend.controller;

import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.OrderCancelDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.vo.OrderDetailVO;
import com.ruomeng.onlineorderingbackend.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理接口
 */
@Api(tags = "订单管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单
     */
    @ApiOperation("分页查询订单")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        log.info("分页查询订单：{}", orderPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询订单详情
     */
    @ApiOperation("根据ID查询订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getById(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        OrderDetailVO orderDetailVO = orderService.getById(id);
        return Result.success(orderDetailVO);
    }

    /**
     * 更新订单
     */
    @ApiOperation("更新订单")
    @PutMapping
    public Result<String> update(@RequestBody OrderDTO orderDTO) {
        log.info("更新订单：{}", orderDTO);
        orderService.update(orderDTO);
        return Result.success("更新成功");
    }

    /**
     * 完成订单
     */
    @ApiOperation("完成订单")
    @PutMapping("/{id}/complete")
    public Result<String> complete(@PathVariable Long id) {
        log.info("完成订单：{}", id);
        orderService.complete(id);
        return Result.success("完成成功");
    }

    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result<String> cancel(@RequestBody OrderCancelDTO orderCancelDTO) {
        log.info("取消订单：{}", orderCancelDTO);
        orderService.cancel(orderCancelDTO);
        return Result.success("取消成功");
    }

    /**
     * 删除订单
     */
    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除订单：{}", id);
        orderService.delete(id);
        return Result.success("删除成功");
    }
}
