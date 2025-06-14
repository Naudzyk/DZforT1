package com.example.DZforT1.service1.service;

import com.example.DZforT1.core.DTO.TransactionCreateDTO;
import com.example.DZforT1.core.DTO.TransactionResponseDTO;

import java.util.List;
import java.util.UUID;


public interface TransactionService {
    List<TransactionResponseDTO> getTransactions();
    TransactionResponseDTO getTransaction(UUID id);
//    TransactionResponseDTO addTransaction(TransactionCreateDTO transactionCreateDTO);

}
