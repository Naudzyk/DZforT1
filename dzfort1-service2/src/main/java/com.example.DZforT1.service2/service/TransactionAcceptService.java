package com.example.DZforT1.service2.service;

import com.example.DZforT1.core.DTO.TransactionAcceptDTO;
import com.example.DZforT1.core.ENUM.TransactionStatus;


public interface TransactionAcceptService {
    void handleAccept(String message);

    void sendResult(TransactionAcceptDTO dto, TransactionStatus status, String reason);
}
