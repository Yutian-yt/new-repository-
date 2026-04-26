package com.stu6136tyt.helloserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 已移除旧的拦截器注册代码，由 Spring Security 接管鉴权
}