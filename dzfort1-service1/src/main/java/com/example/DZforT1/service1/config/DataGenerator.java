package com.example.DZforT1.service1.config;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

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
        List<Client> clients = generateClients(10);
        clientRepository.saveAll(clients);

        List<Account> accounts = generateAccounts(clients, 1, 3);
        accountRepository.saveAll(accounts);

        generateTransactions(accounts, 5, 20);
    }

    private List<Client> generateClients(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    Client client = new Client();
                    client.setFirstName(faker.name().firstName());
                    client.setLastName(faker.name().lastName());
                    client.setMiddleName(faker.name().firstName());
                    client.setClientId(UUID.randomUUID());
                    return client;
                })
                .toList();
    }

    private List<Account> generateAccounts(List<Client> clients, int minPerClient, int maxPerClient) {
        return clients.stream()
                .flatMap(client -> java.util.stream.IntStream
                        .range(0, random.nextInt(maxPerClient - minPerClient + 1) + minPerClient)
                        .mapToObj(i -> {
                            Account account = new Account();
                            account.setAccountType(faker.options().option(AccountType.class));
                            account.setBalance(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 1000000)));
                            account.setClient(client);
                            return account;
                        }))
                .toList();
    }

    private void generateTransactions(List<Account> accounts, int minPerAccount, int maxPerAccount) {
        accounts.forEach(account -> {
            int transactionsCount = random.nextInt(maxPerAccount - minPerAccount + 1) + minPerAccount;
            java.util.stream.IntStream.range(0, transactionsCount)
                    .forEach(i -> {
                        Transaction transaction = new Transaction();
                        transaction.setAmount(BigDecimal.valueOf(faker.number().randomDouble(2, -50000, 50000)));
                        transaction.setTime(LocalDateTime.now().minusDays(random.nextInt(365)));
                        transaction.setAccount(account);
                        transactionRepository.save(transaction);
                    });
        });
    }
}
