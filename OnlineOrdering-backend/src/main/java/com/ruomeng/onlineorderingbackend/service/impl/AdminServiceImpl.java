package com.ruomeng.onlineorderingbackend.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.AdminMapper;
import com.ruomeng.onlineorderingbackend.model.dto.AdminLoginDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Admin;
import com.ruomeng.onlineorderingbackend.model.vo.AdminLoginVO;
import com.ruomeng.onlineorderingbackend.service.AdminService;
import com.ruomeng.onlineorderingbackend.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理员服务实现
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AdminLoginVO login(AdminLoginDTO loginDTO) {
        // 参数校验
        if (!StringUtils.hasText(loginDTO.getUsername()) || !StringUtils.hasText(loginDTO.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");
        }

        // 查询管理员
        Admin admin = adminMapper.selectByUsername(loginDTO.getUsername());
        if (admin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), admin.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername());

        // 构建响应
        AdminLoginVO loginVO = new AdminLoginVO();
        loginVO.setUsername(admin.getUsername());
        loginVO.setToken(token);

        return loginVO;
    }
}
