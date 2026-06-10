package com.ruomeng.onlineorderingbackend.controller.user;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.constant.JwtConstant;
import com.ruomeng.onlineorderingbackend.model.entity.Customer;
import com.ruomeng.onlineorderingbackend.model.vo.CustomerVO;
import com.ruomeng.onlineorderingbackend.model.vo.PointsRecordVO;
import com.ruomeng.onlineorderingbackend.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户端顾客接口
 */
@Api(tags = "用户端-顾客接口")
@Slf4j
@RestController
@RequestMapping("/user/customer")
public class UserCustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取顾客积分
     */
    @ApiOperation("获取顾客积分")
    @GetMapping("/points")
    public Result<Integer> getPoints(HttpServletRequest request) {
        Long customerId = (Long) request.getAttribute(JwtConstant.USER_ID);
        log.info("用户端获取顾客积分，customerId={}", customerId);
        Integer points = customerService.getCustomerPoints(customerId);
        return Result.success(points);
    }

    /**
     * 获取顾客信息
     */
    @ApiOperation("获取顾客信息")
    @GetMapping("/info")
    public Result<CustomerVO> getInfo(HttpServletRequest request) {
        Long customerId = (Long) request.getAttribute(JwtConstant.USER_ID);
        log.info("用户端获取顾客信息，customerId={}", customerId);
        CustomerVO customerVO = customerService.getById(customerId);
        return Result.success(customerVO);
    }

    /**
     * 获取顾客积分记录
     */
    @ApiOperation("获取顾客积分记录")
    @GetMapping("/points-records")
    public Result<List<PointsRecordVO>> getPointsRecords(HttpServletRequest request) {
        Long customerId = (Long) request.getAttribute(JwtConstant.USER_ID);
        log.info("用户端获取顾客积分记录，customerId={}", customerId);
        List<PointsRecordVO> records = customerService.getPointsRecords(customerId);
        return Result.success(records);
    }

    /**
     * 加入会员
     */
    @ApiOperation("加入会员")
    @PostMapping("/join-member")
    public Result<String> joinMember(HttpServletRequest request) {
        Long customerId = (Long) request.getAttribute(JwtConstant.USER_ID);
        log.info("用户端加入会员，customerId={}", customerId);
        customerService.joinMember(customerId);
        return Result.success("加入会员成功");
    }

    /**
     * 更新用户信息
     */
    @ApiOperation("更新用户信息")
    @PutMapping("/update-info")
    public Result<String> updateInfo(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
        Long customerId = (Long) request.getAttribute(JwtConstant.USER_ID);
        String nickname = (String) requestBody.get("nickname");
        String avatar = (String) requestBody.get("avatar");
        Integer gender = requestBody.get("gender") != null ? Integer.valueOf(requestBody.get("gender").toString()) : null;
        
        log.info("用户端更新用户信息，customerId={}, nickname={}, avatar={}, gender={}", customerId, nickname, avatar, gender);
        customerService.updateCustomerInfo(customerId, nickname, avatar, gender);
        return Result.success("更新成功");
    }
}
