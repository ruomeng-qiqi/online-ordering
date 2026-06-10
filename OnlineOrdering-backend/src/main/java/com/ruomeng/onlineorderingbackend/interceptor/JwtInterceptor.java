package com.ruomeng.onlineorderingbackend.interceptor;

import com.ruomeng.onlineorderingbackend.constant.JwtConstant;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.AdminMapper;
import com.ruomeng.onlineorderingbackend.mapper.CustomerMapper;
import com.ruomeng.onlineorderingbackend.model.entity.Admin;
import com.ruomeng.onlineorderingbackend.model.entity.Customer;
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

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
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
            
            // 5. 根据请求路径判断是管理员还是用户，并检查账号状态
            String requestPath = request.getRequestURI();
            
            if (requestPath.startsWith("/admin")) {
                // 管理员请求 - 检查管理员是否存在
                Admin admin = adminMapper.selectById(userId);
                if (admin == null) {
                    log.warn("管理员不存在，adminId: {}", userId);
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "管理员不存在");
                }
                // 注意：管理员表没有 status 字段，所以不需要检查状态
            } else if (requestPath.startsWith("/user")) {
                // 用户请求 - 检查顾客是否存在及账号状态
                Customer customer = customerMapper.selectById(userId);
                if (customer == null) {
                    log.warn("顾客不存在，customerId: {}", userId);
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
                }
                
                if (customer.getStatus() == 0) {
                    log.warn("顾客账号已被禁用，customerId: {}", userId);
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "您的账号已被禁用");
                }
            }
            
            request.setAttribute(JwtConstant.USER_ID, userId);
            request.setAttribute(JwtConstant.USERNAME, username);
            
            log.info("Token 验证成功，用户ID: {}, 用户名: {}", userId, username);
            return true;
        } catch (BusinessException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("Token 解析失败", e);
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "Token 解析失败");
        }
    }
}
