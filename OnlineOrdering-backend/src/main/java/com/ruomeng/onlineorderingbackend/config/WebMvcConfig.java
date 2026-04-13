package com.ruomeng.onlineorderingbackend.config;

import com.ruomeng.onlineorderingbackend.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private WxMaProperties wxMaProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要验证的路径
                .excludePathPatterns(
                        "/health",                  // 健康检查
                        "/admin/login",             // 管理员登录
                        "/admin/logout",            // 管理员登出
                        "/doc.html",                // Knife4j 文档首页
                        "/swagger-ui.html",         // Swagger UI
                        "/webjars/**",              // Knife4j 静态资源
                        "/swagger-resources/**",    // Swagger 资源
                        "/v2/api-docs",             // Swagger 2 API 文档
                        "/favicon.ico",             // 网站图标
                        "/error",                   // 错误页面
                        "/qrcode/**"                // 小程序码图片
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置小程序码图片访问路径
        // 将 /qrcode/** 映射到外部目录
        registry.addResourceHandler("/qrcode/**")
                .addResourceLocations("file:" + wxMaProperties.getQrcodePath());
    }
}
