package com.example.DZforT1.service;

import com.example.DZforT1.DTO.TransactionCreateDTO;
import com.example.DZforT1.DTO.TransactionResponseDTO;
import com.example.DZforT1.models.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TransactionService {
    List<TransactionResponseDTO> getTransactions();
    TransactionResponseDTO getTransaction(Long id);
    TransactionResponseDTO addTransaction(TransactionCreateDTO transactionCreateDTO);

}
