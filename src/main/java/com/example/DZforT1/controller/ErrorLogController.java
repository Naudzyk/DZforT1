package com.example.DZforT1.controller;

import com.example.DZforT1.models.DataSourceErrorLog;
import com.example.DZforT1.repository.DataSourceErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/errors")
@RequiredArgsConstructor
public class ErrorLogController {

    private final DataSourceErrorLogRepository repository;

    @GetMapping
    public List<DataSourceErrorLog> getAllErrors() {
        return repository.findAll();
    }
}
