package com.ruomeng.onlineorderingbackend.service;

import com.ruomeng.onlineorderingbackend.model.dto.UserLoginDTO;
import com.ruomeng.onlineorderingbackend.model.vo.UserLoginVO;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 微信小程序登录
     */
    UserLoginVO login(UserLoginDTO userLoginDTO);
}
