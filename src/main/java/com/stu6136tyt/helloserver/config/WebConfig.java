package com.stu6136tyt.helloserver.config;

import com.stu6136tyt.helloserver.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**") // 拦截 /api 下的所有请求路径
                .excludePathPatterns(
                        "/api/users/login",   // 保留原本放行的登录接口
                        "/api/users",         // 放行：新增用户接口（匹配精确的 /api/users）
                        "/api/users/*"        // 放行：获取用户信息接口（匹配 1001 等 ID）
                );
    }
}