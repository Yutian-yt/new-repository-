package com.stu6136tyt.helloserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-millis}")
    private long expirationMillis;

    /**
     * 根据配置文件中的 secret 生成签名密钥
     */
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(username)      // 主题：这里存用户名
                .issuedAt(now)          // 签发时间
                .expiration(expireDate) // 过期时间
                .signWith(getSignKey()) // 签名
                .compact();
    }

    /**
     * 解析全部 Claims
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                // 注意：图片截断处通常紧接着是 .parseSignedClaims(token).getPayload();
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String jwt) {
        return jwt;
    }
}