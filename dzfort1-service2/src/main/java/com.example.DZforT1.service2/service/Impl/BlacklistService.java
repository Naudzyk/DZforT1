package com.example.DZforT1.service2.service.Impl;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class BlacklistService {

    private final Random random = new Random();

    public boolean isClientBlacklisted(UUID clientId, UUID accountId) {
        // Пример: 20% вероятность нахождения в черном списке
        return random.nextInt(100) < 20;
    }
}
