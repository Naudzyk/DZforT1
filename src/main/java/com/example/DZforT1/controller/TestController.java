package com.example.DZforT1.controller;

import com.example.DZforT1.aop.LogDataSourceAOP.LogDataSourceError;
import com.example.DZforT1.aop.MetricAOP.Metric;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test-slow")
    @Metric
    public String testSlowMethod() throws InterruptedException {
        Thread.sleep(800);
        return "Slow method executed";
    }

    @GetMapping("/test-db-error")
    @LogDataSourceError
    public String testDbError() {
        throw new DataAccessResourceFailureException("DB error");
    }
}
