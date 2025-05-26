package com.example.DZforT1.aop;

import com.example.DZforT1.models.DataSourceErrorLog;

import com.example.DZforT1.service.DataSourceErrorLogService;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;


@Aspect
@Component
@RequiredArgsConstructor
public class DataSourceErrorAspect {
    private final DataSourceErrorLogService errorLogService;


    @Pointcut("@annotation(LogDataSourceError)")
    public void logDataSourceErrorMethods() {}

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterThrowing(pointcut = "logDataSourceErrorMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            transactionTemplate.execute(status -> {
                try {
                    DataSourceErrorLog log = new DataSourceErrorLog();
                    log.setExceptionMessage(ex.getMessage());
                    log.setStackTrace(ex.getStackTrace().length > 0 ? Arrays.toString(ex.getStackTrace()) : "No stack trace");
                    log.setMethodSignature(joinPoint.getSignature().toShortString());
                    log.setTimestamp(LocalDateTime.now());
                    errorLogService.saveError(log);
                } catch (Exception e) {
                    System.err.println("Ошибка при сохранении лога: " + e.getMessage());
                    status.setRollbackOnly();
                }
                return null;
            });
        }
}
