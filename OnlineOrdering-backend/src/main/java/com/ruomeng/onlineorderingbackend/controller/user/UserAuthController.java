package com.ruomeng.onlineorderingbackend.controller.user;

import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.model.dto.UserLoginDTO;
import com.ruomeng.onlineorderingbackend.model.vo.UserLoginVO;
import com.ruomeng.onlineorderingbackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端认证接口
 */
@Api(tags = "用户端-认证接口")
@Slf4j
@RestController
@RequestMapping("/user/auth")
public class UserAuthController {

    @Autowired
    private UserService userService;

    /**
     * 微信小程序登录
     */
    @ApiOperation("微信小程序登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户端微信登录，code={}", userLoginDTO.getCode());
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        return Result.success(userLoginVO);
    }
}
