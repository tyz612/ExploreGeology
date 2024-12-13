package com.geology.user.common.utils;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class GenerateTokenUtil {
    private String secretKey = "QPzm&9135"; // 应该是一个安全的密钥

    public String login(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 设置 token 有效期为1小时
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return "Bearer " + token;
    }
}
