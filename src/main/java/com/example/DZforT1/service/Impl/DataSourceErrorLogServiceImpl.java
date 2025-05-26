package com.example.DZforT1.service.Impl;

import com.example.DZforT1.models.DataSourceErrorLog;
import com.example.DZforT1.repository.DataSourceErrorLogRepository;
import com.example.DZforT1.service.DataSourceErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {
    private final DataSourceErrorLogRepository repository;
    @Override
    public List<DataSourceErrorLog> getAllErrors() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public DataSourceErrorLog saveError(DataSourceErrorLog errorLog) {
        return repository.save(errorLog);
    }
}
