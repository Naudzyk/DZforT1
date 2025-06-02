package com.example.DZforT1.aop.LogDataSourceAOP;

import com.example.DZforT1.models.DataSourceErrorLog;

import com.example.DZforT1.service.DataSourceErrorLogService;


import com.example.DZforT1.service.ErrorLoggingService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;


@Aspect
@Component
@RequiredArgsConstructor
public class DataSourceErrorAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ErrorLoggingService errorLoggingService;


    @Pointcut("@annotation(LogDataSourceError)")
    public void logDataSourceErrorMethods() {}

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterThrowing(pointcut = "logDataSourceErrorMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
        try {
           errorLoggingService.logError(joinPoint, ex);
        }catch (Exception e) {
            System.err.println("Ошибка при сохранении лога: " + e.getMessage());
        }


        }
}
