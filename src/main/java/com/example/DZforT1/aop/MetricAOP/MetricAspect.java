package com.example.DZforT1.aop.MetricAOP;

import com.example.DZforT1.service.TimeLimitExcedLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final TimeLimitExcedLogService timeLimitExcedLogService;


    @Value("${app.metric.limit-millis}")
    private long limitMillis;

    @Around("@annotation(com.example.DZforT1.aop.MetricAOP.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            if (duration > limitMillis) {
                String methodName = joinPoint.getSignature().toShortString();
                timeLimitExcedLogService.logExceedMethod(methodName, duration);
            }
        }
    }
}
