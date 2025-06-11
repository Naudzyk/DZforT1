package com.example.DZforT1.service1.config;

import com.example.DZforT1.core.ENUM.AccountStatus;
import com.example.DZforT1.core.ENUM.TransactionStatus;
import com.example.DZforT1.service1.models.Account;
import com.example.DZforT1.core.ENUM.AccountType;
import com.example.DZforT1.service1.models.Client;
import com.example.DZforT1.service1.models.Transaction;
import com.example.DZforT1.service1.repository.AccountRepository;
import com.example.DZforT1.service1.repository.ClientRepository;
import com.example.DZforT1.service1.repository.TransactionRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataGenerator implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Faker faker = new Faker(new Locale("ru"));
    private final Random random = new Random();

    @Override
    public void run(String... args) {
        // 1. Генерируем клиентов и сохраняем их в БД
        List<Client> clients = generateClients(10);
        List<Client> savedClients = clientRepository.saveAll(clients);

        // 2. Генерируем аккаунты и сохраняем их в БД
        List<Account> accounts = generateAccounts(savedClients, 1, 3);
        List<Account> savedAccounts = accountRepository.saveAll(accounts);

        // 3. Генерируем транзакции
        generateTransactions(savedAccounts, 5, 20);
    }

    // --- Методы генерации ---

    private List<Client> generateClients(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Client client = new Client();
                    client.setFirstName(faker.name().firstName());
                    client.setLastName(faker.name().lastName());
                    client.setMiddleName(faker.name().nameWithMiddle());
                    client.setClientId(UUID.randomUUID());
                    return client;
                })
                .toList();
    }

    private List<Account> generateAccounts(List<Client> clients, int minPerClient, int maxPerClient) {
        return clients.stream()
                .flatMap(client -> IntStream.range(0, random.nextInt(maxPerClient - minPerClient + 1) + minPerClient)
                        .mapToObj(i -> {
                            Account account = new Account();
                            AccountType[] types = AccountType.values();
                            account.setAccountType(types[random.nextInt(types.length)]);
                            account.setBalance(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 1000000)));
                            account.setFrozenAmount(BigDecimal.ZERO);
                            account.setStatus(AccountStatus.OPEN);
                            account.setClient(client); // Устанавливаем связь
                            account.setClientId(client.getClientId()); // Явно устанавливаем clientId
                            account.setAccountId(UUID.randomUUID()); // Генерируем accountId явно
                            return account;
                        }))
                .toList();
    }

    @Transactional
    public void generateTransactions(List<Account> accounts, int minPerAccount, int maxPerAccount) {
        accounts.forEach(account -> {
            int transactionCount = random.nextInt(maxPerAccount - minPerAccount + 1) + minPerAccount;

            for (int i = 0; i < transactionCount; i++) {
                Transaction transaction = new Transaction();
                transaction.setClientId(account.getClientId()); // ✅ Убедитесь, что account.clientId установлен
                transaction.setAccountId(account.getAccountId()); // ✅ Убедитесь, что account.accountId установлен
                transaction.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, -50000, 50000)));
                transaction.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(365)));
                transaction.setStatus(TransactionStatus.REQUESTED);

                transactionRepository.save(transaction);
            }
        });
    }
}
