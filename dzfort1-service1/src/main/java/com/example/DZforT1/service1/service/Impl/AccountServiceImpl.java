package com.example.DZforT1.service1.service.Impl;
import com.example.DZforT1.core.DTO.AccountCreateDTO;
import com.example.DZforT1.core.DTO.AccountResponseDTO;
import com.example.DZforT1.core.DTO.AccountUpdateDTO;
import com.example.DZforT1.core.ENUM.AccountStatus;
import com.example.DZforT1.service1.aop.CachedAOP.Cached;
import com.example.DZforT1.service1.aop.LogDataSourceAOP.LogDataSourceError;
import com.example.DZforT1.service1.aop.MetricAOP.Metric;
import com.example.DZforT1.service1.models.Account;
import com.example.DZforT1.service1.models.Client;
import com.example.DZforT1.service1.repository.AccountRepository;
import com.example.DZforT1.service1.repository.ClientRepository;
import com.example.DZforT1.service1.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


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
            @Cached
            @Metric
            public AccountResponseDTO getAccountById(UUID id) {
                return accountRepository.findByAccountId(id)
                        .map(this::convertToDto)
                        .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
            }

            /**
             * Получить все аккаунты
             */
            @Override
            @Transactional
            @Metric
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
            public AccountResponseDTO createAccount(@Validated AccountCreateDTO dto) {
                Client owner = clientRepository.findById(dto.clientId())
                        .orElseThrow(() -> new RuntimeException("Client with ID " + dto.clientId() + " not found"));

                if (dto.balance().compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Balance cannot be negative");
                }


                Account account = new Account();
                account.setAccountId(UUID.randomUUID());
                account.setClient(owner);
                account.setClientId(owner.getClientId());
                account.setAccountType(dto.accountType());
                account.setBalance(dto.balance());
                account.setFrozenAmount(BigDecimal.ZERO);
                account.setStatus(AccountStatus.OPEN);


                Account saved = accountRepository.save(account);
                return convertToDto(saved);
            }

            /**
             * Обновить аккаунт
             */
            @Override
            @Transactional
            @LogDataSourceError
            public AccountResponseDTO updateAccount(UUID id, AccountUpdateDTO dto) {
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
            public void deleteAccount(UUID id) {
                if (!accountRepository.existsById(id)) {
                    throw new RuntimeException("Account not found with ID: " + id);
                }
                accountRepository.deleteById(id);
            }


                private AccountResponseDTO convertToDto(Account account) {
                    return new AccountResponseDTO(
                        account.getAccountId(),
                        account.getStatus(),
                        account.getBalance(),
                        account.getFrozenAmount()
                    );
                }
}