package com.example.DZforT1.service1.service.Impl;

import com.example.DZforT1.core.DTO.AccountResponseDTO;
import com.example.DZforT1.core.DTO.ClientCreateDTO;
import com.example.DZforT1.core.DTO.ClientResponseDTO;

import com.example.DZforT1.service1.aop.CachedAOP.Cached;
import com.example.DZforT1.service1.aop.LogDataSourceAOP.LogDataSourceError;
import com.example.DZforT1.service1.aop.MetricAOP.Metric;
import com.example.DZforT1.service1.models.Account;
import com.example.DZforT1.service1.models.Client;

import com.example.DZforT1.service1.repository.ClientRepository;
import com.example.DZforT1.service1.service.ClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    @LogDataSourceError
    public ClientResponseDTO createClient(ClientCreateDTO dto) {
        if (dto.firstName() == null || dto.lastName() == null || dto.middleName() == null) {
            throw new IllegalArgumentException("First name, last name and middle name are required");
        }

        Client client = new Client();
        client.setFirstName(dto.firstName());
        client.setLastName(dto.lastName());
        client.setMiddleName(dto.middleName());
        client.setClientId(UUID.randomUUID());

        Client saved = clientRepository.save(client);

        return convertToClientResponseDTO(saved);
    }

    @Override
    @Transactional
    @LogDataSourceError
    public ClientResponseDTO updateClient(UUID id, ClientCreateDTO dto) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        existing.setFirstName(dto.firstName());
        existing.setLastName(dto.lastName());
        existing.setMiddleName(dto.middleName());

        Client updated = clientRepository.save(existing);
        return convertToClientResponseDTO(updated);
    }


    @Override
    @Transactional
    @LogDataSourceError
    @Metric
    @Cached
    public ClientResponseDTO getClientById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return convertToClientResponseDTO(client);
    }

    @Override
    @Transactional
    @Metric
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToClientResponseDTO)
                .toList();
    }


    @Override
    @Transactional
    @LogDataSourceError
    public void deleteClient(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found with ID: " + id);
        }
        clientRepository.deleteById(id);
    }


    private ClientResponseDTO convertToClientResponseDTO(Client client) {
        List<AccountResponseDTO> accountDTOS = client.getAccounts().stream()
                .map(this::convertToAccountResponseDTO)
                .toList();

        return new ClientResponseDTO(
            client.getFirstName(),
            client.getLastName(),
            client.getMiddleName(),
            client.getStatus(),
            accountDTOS,
            client.getClientId()
        );
    }

    private AccountResponseDTO convertToAccountResponseDTO(Account account) {
        return new AccountResponseDTO(
            account.getAccountId(),
            account.getStatus(),
            account.getBalance(),
            account.getFrozenAmount()
        );
    }
}