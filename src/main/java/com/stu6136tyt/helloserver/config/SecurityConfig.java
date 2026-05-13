package com.stu6136tyt.helloserver.config;

import com.stu6136tyt.helloserver.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 开启全局 CORS 配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 关闭 CSRF 防护，前后端分离场景下常用
                .csrf(csrf -> csrf.disable())
                // 配置 Session 管理策略，设置无状态
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 放行 POST /api/users/login
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        // 放行 POST /api/users
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // 放行 POST /api/chat
                        .requestMatchers(HttpMethod.POST, "/api/chat").permitAll()
                        // 其他所有请求都必须先认证
                        .anyRequest().authenticated()
                )
                // 添加 JWT 过滤器，放在 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 关闭 Spring Security 默认的表单登录
                .formLogin(form -> form.disable())
                // 关闭 httpBasic 认证
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源
        configuration.setAllowedOrigins(List.of("*"));
        // 允许所有方法
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许所有请求头
        configuration.setAllowedHeaders(List.of("*"));
        // 允许携带凭证
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用 CORS 配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}