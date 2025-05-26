package com.example.DZforT1.service;

import com.example.DZforT1.DTO.ClientCreateDTO;
import com.example.DZforT1.DTO.ClientResponseDTO;
import com.example.DZforT1.DTO.ClientUpdateDTO;
import jakarta.transaction.Transactional;

import java.util.List;


public interface ClientService {
    List<ClientResponseDTO> getAllClients();
    ClientResponseDTO getClientById(Long id);
    ClientResponseDTO createClient (ClientCreateDTO clientCreateDTO);
    ClientResponseDTO updateClient(Long id, ClientCreateDTO dto);
    void deleteClient (Long id);
}
