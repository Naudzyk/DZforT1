package com.example.DZforT1.service1.service;

import com.example.DZforT1.core.DTO.AccountCreateDTO;
import com.example.DZforT1.core.DTO.AccountResponseDTO;
import com.example.DZforT1.core.DTO.AccountUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponseDTO getAccountById(UUID id);
    List<AccountResponseDTO> getAllAccounts();
    AccountResponseDTO createAccount(AccountCreateDTO accountCreateDTO);
    AccountResponseDTO updateAccount(UUID id, AccountUpdateDTO dto);
    void deleteAccount(UUID id);

}
