package com.example.DZforT1.service;

import org.aspectj.lang.JoinPoint;

public interface ErrorLoggingService {

    void logError(JoinPoint joinPoint, Exception ex);

}
