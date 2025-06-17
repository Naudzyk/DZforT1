package com.example.DZforT1.service2.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private final SecretKey secretKey = Keys.hmacShaKeyFor("mySecureSecretKeyThatIsLongEnoughForHS512".getBytes(StandardCharsets.UTF_8));

    public String generateToken() {
        return Jwts.builder()
            .setSubject("service-check")
            .setExpiration(new Date(System.currentTimeMillis() + 60_000))
            .signWith(secretKey)
            .compact();
    }
    @PostConstruct
    public void checkToken() {
        log.info("Сгенерированный токен: {}", generateToken());
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            log.warn("Токен недействителен: {}", token);
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}