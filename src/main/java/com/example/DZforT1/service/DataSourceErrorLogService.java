package com.example.DZforT1.service;

import com.example.DZforT1.models.DataSourceErrorLog;

import java.util.List;

public interface DataSourceErrorLogService {
        DataSourceErrorLog saveError(DataSourceErrorLog errorLog);
        List<DataSourceErrorLog> getAllErrors();
}
