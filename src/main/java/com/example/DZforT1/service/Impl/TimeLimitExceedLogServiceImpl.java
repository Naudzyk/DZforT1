package com.example.DZforT1.service.Impl;

import com.example.DZforT1.models.TimeLimitExceedLog;
import com.example.DZforT1.repository.TimeLimitExceedLogRepository;
import com.example.DZforT1.service.TimeLimitExcedLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimeLimitExceedLogServiceImpl implements TimeLimitExcedLogService {

    private final TimeLimitExceedLogRepository timeLimitExceedLogRepository;


    @Override
    public void logExceedMethod(String nameOfMethod, Long duration) {
        TimeLimitExceedLog log = new TimeLimitExceedLog();
        log.setNameOfMethod(nameOfMethod);
        log.setDuration(duration);
        log.setStampTime(LocalDateTime.now());
        timeLimitExceedLogRepository.save(log);
    }
}
