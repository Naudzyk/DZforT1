package com.example.DZforT1.service1;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private final long expiration = 60_000; // 60 секунд

    // Генерация токена (сервис 1)
    public String generateToken() {
        return Jwts.builder()
            .setSubject("service-check") // Имя пользователя/сервиса
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(signatureAlgorithm, secretKey)
            .compact();
    }

    // Проверка токена (сервис 2)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    // Извлечение username из токена (сервис 2)
    public String extractUsername(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
