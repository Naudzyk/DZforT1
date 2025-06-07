package com.example.DZforT1.service1.controller;

import com.example.DZforT1.service1.models.DataSourceErrorLog;
import com.example.DZforT1.service1.service.DataSourceErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/errors")
@RequiredArgsConstructor
public class ErrorLogController {

    private final DataSourceErrorLogService dataSourceErrorLogService;

    @GetMapping
    public List<DataSourceErrorLog> getAllErrors() {
        return dataSourceErrorLogService.getAllErrors();
    }
}
