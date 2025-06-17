package com.example.DZforT1.service1.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface TransactionProcesingService {
    void processTransaction(String message) throws JsonProcessingException;


}
