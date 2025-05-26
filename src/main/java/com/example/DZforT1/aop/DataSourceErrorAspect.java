package com.example.DZforT1.aop;

import com.example.DZforT1.models.DataSourceErrorLog;

import com.example.DZforT1.service.DataSourceErrorLogService;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;


@Aspect
@Component
@RequiredArgsConstructor
public class DataSourceErrorAspect {
    private final DataSourceErrorLogService errorLogService;


    @Pointcut("@annotation(LogDataSourceError)")
    public void logDataSourceErrorMethods() {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AfterThrowing(pointcut = "logDataSourceErrorMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setExceptionMessage(ex.getMessage());
        errorLog.setStackTrace(Arrays.toString(ex.getStackTrace()));
        errorLog.setMethodSignature(joinPoint.getSignature().toString());
        errorLog.setTimestamp(LocalDateTime.now());

        errorLogService.saveError(errorLog);
    }
}
