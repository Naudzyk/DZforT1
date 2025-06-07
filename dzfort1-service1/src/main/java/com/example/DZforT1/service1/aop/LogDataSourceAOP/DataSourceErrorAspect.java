package com.example.DZforT1.service1.aop.LogDataSourceAOP;


import com.example.DZforT1.service1.service.ErrorLoggingService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;


@Aspect
@Component
@RequiredArgsConstructor
public class DataSourceErrorAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ErrorLoggingService errorLoggingService;


    @Pointcut("@annotation(com.example.DZforT1.service1.aop.LogDataSourceAOP.LogDataSourceError)")
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
