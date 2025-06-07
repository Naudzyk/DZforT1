package com.example.DZforT1.service1.controller;

import com.example.DZforT1.core.DTO.ClientCreateDTO;
import com.example.DZforT1.core.DTO.ClientResponseDTO;
import com.example.DZforT1.service1.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /**
     * Создает нового клиента на основе предоставленных данных
     *
     * @param dto DTO-объект с данными для создания клиента
     * @return ResponseEntity с созданным клиентом в формате DTO и статусом 201 (Created)
     */
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@RequestBody ClientCreateDTO dto) {
        ClientResponseDTO created = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получает клиента по указанному идентификатору
     *
     * @param id Идентификатор клиента
     * @return ResponseEntity с данными клиента в формате DTO и статусом 200 (OK),
     *         либо статусом 404 (Not Found) если клиент не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    /**
     * Получает список всех зарегистрированных клиентов
     *
     * @return ResponseEntity со списком клиентов в формате DTO и статусом 200 (OK),
     *         либо пустым списком если клиенты отсутствуют
     */
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    /**
     * Обновляет данные клиента по указанному идентификатору
     *
     * @param id Идентификатор клиента для обновления
     * @param dto DTO-объект с новыми данными клиента
     * @return ResponseEntity с обновленными данными клиента в формате DTO и статусом 200 (OK),
     *         либо статусом 404 (Not Found) если клиент не найден
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable UUID id, @RequestBody ClientCreateDTO dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    /**
     * Удаляет клиента по указанному идентификатору
     *
     * @param id Идентификатор клиента для удаления
     * @return ResponseEntity со статусом 204 (No Content) при успешном удалении,
     *         либо статусом 404 (Not Found) если клиент не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
