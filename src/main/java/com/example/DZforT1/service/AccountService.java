package com.example.DZforT1.service;

import com.example.DZforT1.DTO.AccountCreateDTO;
import com.example.DZforT1.DTO.AccountResponseDTO;
import com.example.DZforT1.DTO.AccountUpdateDTO;
import com.example.DZforT1.models.Account;

import java.util.List;

public interface AccountService {
    AccountResponseDTO getAccountById(Long id);
    List<AccountResponseDTO> getAllAccounts();
    AccountResponseDTO createAccount(AccountCreateDTO accountCreateDTO);
    AccountResponseDTO updateAccount(Long id, AccountUpdateDTO dto);
    void deleteAccount(Long id);

}
