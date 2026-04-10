package com.ruomeng.onlineorderingbackend.controller;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.AdminLoginDTO;
import com.ruomeng.onlineorderingbackend.model.vo.AdminLoginVO;
import com.ruomeng.onlineorderingbackend.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员接口
 */
@Api(tags = "管理员接口")
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO loginDTO) {
        log.info("管理员登录请求：{}", loginDTO.getUsername());
        AdminLoginVO loginVO = adminService.login(loginDTO);
        return Result.success(loginVO);
    }

    /**
     * 管理员登出
     */
    @ApiOperation("管理员登出")
    @PostMapping("/logout")
    public Result<String> logout() {
        log.info("管理员登出");
        return Result.success("登出成功");
    }
}
