package com.ruomeng.onlineorderingbackend.controller.user;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.OrderCancelDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderDTO;
import com.ruomeng.onlineorderingbackend.model.dto.PaymentDTO;
import com.ruomeng.onlineorderingbackend.model.vo.OrderVO;
import com.ruomeng.onlineorderingbackend.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端订单接口
 */
@Api(tags = "用户端-订单接口")
@Slf4j
@RestController
@RequestMapping("/user/order")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @ApiOperation("创建订单")
    @PostMapping
    public Result<OrderVO> createOrder(@RequestBody OrderDTO orderDTO) {
        log.info("用户端创建订单，orderDTO={}", orderDTO);
        OrderVO orderVO = orderService.createOrder(orderDTO);
        return Result.success(orderVO);
    }

    /**
     * 查询订单列表
     */
    @ApiOperation("查询订单列表")
    @GetMapping("/list")
    public Result<List<OrderVO>> list(@RequestParam Long customerId) {
        log.info("用户端查询订单列表，customerId={}", customerId);
        List<OrderVO> orders = orderService.listByCustomerId(customerId);
        return Result.success(orders);
    }

    /**
     * 根据ID查询订单详情
     */
    @ApiOperation("根据ID查询订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("用户端查询订单详情，id={}", id);
        OrderVO orderVO = orderService.getByIdWithDetails(id);
        return Result.success(orderVO);
    }

    /**
     * 查询餐台的待支付订单
     */
    @ApiOperation("查询餐台的待支付订单")
    @GetMapping("/pending")
    public Result<OrderVO> getPendingOrder(@RequestParam Long tableId) {
        log.info("用户端查询餐台待支付订单，tableId={}", tableId);
        OrderVO orderVO = orderService.getPendingOrderByTableId(tableId);
        return Result.success(orderVO);
    }

    /**
     * 更新订单（追加菜品）
     * 只能更新待支付状态的订单
     */
    @ApiOperation("更新订单")
    @PutMapping
    public Result<String> updateOrder(@RequestBody OrderDTO orderDTO) {
        log.info("用户端更新订单，orderDTO={}", orderDTO);
        orderService.update(orderDTO);
        return Result.success("更新成功");
    }

    /**
     * 支付订单
     */
    @ApiOperation("支付订单")
    @PostMapping("/pay")
    public Result<String> payOrder(@RequestBody PaymentDTO paymentDTO) {
        log.info("用户端支付订单，paymentDTO={}", paymentDTO);
        orderService.payOrder(paymentDTO);
        return Result.success("支付成功");
    }

    /**
     * 删除订单（软删除，仅用户端隐藏）
     * 只能删除已取消的订单
     */
    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<String> deleteOrder(@PathVariable Long id) {
        log.info("用户端删除订单，id={}", id);
        orderService.deleteOrderByUser(id);
        return Result.success("删除成功");
    }
}
