package com.ruomeng.onlineorderingbackend.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.util.RandomUtil;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.UserMapper;
import com.ruomeng.onlineorderingbackend.model.dto.UserLoginDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Customer;
import com.ruomeng.onlineorderingbackend.model.vo.UserLoginVO;
import com.ruomeng.onlineorderingbackend.service.UserService;
import com.ruomeng.onlineorderingbackend.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 微信小程序登录
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        // 1. 校验参数
        if (userLoginDTO.getCode() == null || userLoginDTO.getCode().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录凭证不能为空");
        }

        // 2. 调用微信接口获取 openid
        String openid;
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(userLoginDTO.getCode());
            openid = session.getOpenid();
            log.info("微信登录成功，openid={}", openid);
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "微信登录失败");
        }

        // 3. 根据 openid 查询用户
        Customer customer = userMapper.selectByOpenid(openid);

        // 4. 如果用户不存在，创建新用户（注册）
        if (customer == null) {
            customer = new Customer();
            customer.setOpenid(openid);
            // 生成随机昵称：用户 + 4位随机数字
            customer.setNickname("用户" + RandomUtil.randomNumbers(8));
            customer.setAvatar(null);
            customer.setGender(0);
            customer.setIsMember(0);
            customer.setPoints(0);
            customer.setTotalPoints(0);
            customer.setStatus(1);
            customer.setCreateTime(LocalDateTime.now());
            customer.setUpdateTime(LocalDateTime.now());
            
            userMapper.insert(customer);
            log.info("新用户注册成功，customerId={}，nickname={}", customer.getId(), customer.getNickname());
        }

        // 5. 检查账号状态
        if (customer.getStatus() == 0) {
            log.warn("账号已被禁用，customerId={}", customer.getId());
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "您的账号已被禁用，无法登录");
        }

        // 6. 生成 JWT Token
        String token = jwtUtil.generateToken(customer.getId(), customer.getNickname());

        // 7. 返回登录信息
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setCustomerId(customer.getId());
        userLoginVO.setToken(token);
        userLoginVO.setNickname(customer.getNickname());
        userLoginVO.setAvatar(customer.getAvatar());
        userLoginVO.setIsMember(customer.getIsMember());
        userLoginVO.setPoints(customer.getPoints());
        userLoginVO.setStatus(customer.getStatus());

        return userLoginVO;
    }
}
