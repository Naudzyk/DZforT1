package com.example.DZforT1.service1.config;

import com.example.DZforT1.service1.JwtService;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(JwtService jwtService) {
        return requestTemplate -> {
            // Генерация токена
            String token = "Bearer " + jwtService.generateToken();
            requestTemplate.header("Authorization", token);
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
