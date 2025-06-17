package com.example.DZforT1.service1.service;

import com.example.DZforT1.core.DTO.ClientCreateDTO;
import com.example.DZforT1.core.DTO.ClientResponseDTO;
import com.example.DZforT1.core.DTO.ClientUpdateDTO;

import java.util.List;
import java.util.UUID;


public interface ClientService {
    List<ClientResponseDTO> getAllClients();
    ClientResponseDTO getClientById(UUID id);
    ClientResponseDTO createClient (ClientCreateDTO clientCreateDTO);
    ClientResponseDTO updateClient(UUID id, ClientCreateDTO dto);
    void deleteClient (UUID id);
}
