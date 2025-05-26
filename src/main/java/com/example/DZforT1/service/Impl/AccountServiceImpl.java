package com.example.DZforT1.service.Impl;
import com.example.DZforT1.DTO.AccountCreateDTO;
import com.example.DZforT1.DTO.AccountResponseDTO;
import com.example.DZforT1.DTO.AccountUpdateDTO;
import com.example.DZforT1.aop.LogDataSourceError;
import com.example.DZforT1.models.Account;
import com.example.DZforT1.models.Client;
import com.example.DZforT1.repository.AccountRepository;
import com.example.DZforT1.repository.ClientRepository;
import com.example.DZforT1.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

            private final AccountRepository accountRepository;
            private final ClientRepository clientRepository;

            /**
             * Получить аккаунт по ID
             */
            @Override
            @Transactional
            @LogDataSourceError
            public AccountResponseDTO getAccountById(Long id) {
                return accountRepository.findById(id)
                        .map(this::convertToDto)
                        .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
            }

            /**
             * Получить все аккаунты
             */
            @Override
            @Transactional
            public List<AccountResponseDTO> getAllAccounts() {
                return accountRepository.findAll().stream()
                        .map(this::convertToDto)
                        .toList();
            }

            /**
             * Создать новый аккаунт
             */
            @Override
            @Transactional
            @LogDataSourceError
            public AccountResponseDTO createAccount(AccountCreateDTO dto) {
                Client owner = clientRepository.findById(dto.clientId())
                        .orElseThrow(() -> new RuntimeException("Client not found"));

                Account account = new Account();
                account.setClient(owner);
                account.setAccountType(dto.accountType());
                account.setBalance(dto.balance());

                Account saved = accountRepository.save(account);
                return convertToDto(saved);
            }

            /**
             * Обновить аккаунт
             */
            @Override
            @Transactional
            @LogDataSourceError
            public AccountResponseDTO updateAccount(Long id, AccountUpdateDTO dto) {
                Account account = accountRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                account.setAccountType(dto.accountType());
                account.setBalance(dto.balance());

                return convertToDto(accountRepository.save(account));
            }

            /**
             * Удалить аккаунт
             */
            @Override
            @Transactional
            @LogDataSourceError
            public void deleteAccount(Long id) {
                if (!accountRepository.existsById(id)) {
                    throw new RuntimeException("Account not found with ID: " + id);
                }
                accountRepository.deleteById(id);
            }


            private AccountResponseDTO convertToDto(Account account) {
                return new AccountResponseDTO(
                    account.getId(),
                    account.getAccountType(),
                    account.getBalance()
                );
            }
}