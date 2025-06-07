package com.example.DZforT1.service1.service;

import com.example.DZforT1.service1.models.DataSourceErrorLog;

import java.util.List;

public interface DataSourceErrorLogService {
        void saveError(DataSourceErrorLog errorLog);
        List<DataSourceErrorLog> getAllErrors();
}
