package com.example.DZforT1.service1.service.Impl;

import com.example.DZforT1.service1.aop.CachedAOP.Cached;
import com.example.DZforT1.service1.aop.MetricAOP.Metric;
import com.example.DZforT1.service1.models.DataSourceErrorLog;
import com.example.DZforT1.service1.repository.DataSourceErrorLogRepository;
import com.example.DZforT1.service1.service.DataSourceErrorLogService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {
    private final DataSourceErrorLogRepository repository;
    @Override
    @Cached
    @Metric
    public List<DataSourceErrorLog> getAllErrors() {
        return repository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveError(DataSourceErrorLog errorLog) {
        DataSourceErrorLog log = new DataSourceErrorLog();
        log.setMethodSignature(errorLog.getMethodSignature());
        log.setExceptionMessage(errorLog.getExceptionMessage());
        log.setStackTrace(Arrays.toString(new String[]{errorLog.getStackTrace()}));
        log.setTimestamp(LocalDateTime.now());

        repository.save(log);
    }
}
