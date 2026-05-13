package com.stu6136tyt.helloserver.security;

import com.stu6136tyt.helloserver.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
        // 1. 读取请求头中的 Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. 如果没有 Authorization, 或者不是 Bearer 开头, 直接放行给后续过滤器
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 截取真正的 JWT 字符串
        String jwt = authHeader.substring(7);

        String username;
        try {
            // 4. 从 JWT 中解析用户名
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            // token 解析失败, 直接继续后续过滤器
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 如果解析到了用户名, 并且当前还没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. 从数据库加载用户详情
            UserDetails userDetails = userMapper.selectByUsername(username);

            // 7. 如果用户存在, 创建认证对象并设置到上下文中
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. 放行请求
        filterChain.doFilter(request, response);
    }
}
