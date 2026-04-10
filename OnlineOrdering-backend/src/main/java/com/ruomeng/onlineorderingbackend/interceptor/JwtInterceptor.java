package com.ruomeng.onlineorderingbackend.interceptor;

import com.ruomeng.onlineorderingbackend.constant.JwtConstant;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 从请求头获取 Token
        String authHeader = request.getHeader(JwtConstant.HEADER_NAME);
        
        if (authHeader == null || !authHeader.startsWith(JwtConstant.TOKEN_PREFIX)) {
            log.warn("请求头中未找到 Token");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请求头中未找到 Token");
        }

        // 2. 去除 "Bearer " 前缀
        String token = authHeader.substring(JwtConstant.TOKEN_PREFIX.length());

        // 3. 验证 Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 无效或已过期");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "Token 无效或已过期");
        }

        // 4. 解析 Token，将用户信息存入请求属性
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            
            request.setAttribute(JwtConstant.USER_ID, userId);
            request.setAttribute(JwtConstant.USERNAME, username);
            
            log.info("Token 验证成功，用户ID: {}, 用户名: {}", userId, username);
            return true;
        } catch (Exception e) {
            log.error("Token 解析失败", e);
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "Token 解析失败");
        }
    }
}
