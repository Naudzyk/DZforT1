package com.example.DZforT1.service1.controller;

import com.example.DZforT1.core.DTO.*;

import com.example.DZforT1.service1.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Создает новый счет на основе предоставленных данных
     *
     * @param dto DTO-объект с данными для создания счета
     * @return ResponseEntity с созданным счетом в формате DTO и статусом 201 (Created)
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountCreateDTO dto) {
        AccountResponseDTO created = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получает счет по указанному идентификатору
     *
     * @param id Идентификатор счета
     * @return ResponseEntity с данными счета в формате DTO и статусом 200 (OK),
     *         либо статусом 404 (Not Found) если счет не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    /**
     * Получает список всех существующих счетов
     *
     * @return ResponseEntity со списком счетов в формате DTO и статусом 200 (OK),
     *         либо пустым списком если счета отсутствуют
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    /**
     * Обновляет данные счета по указанному идентификатору
     *
     * @param id Идентификатор счета для обновления
     * @param dto DTO-объект с новыми данными счета
     * @return ResponseEntity с обновленными данными счета в формате DTO и статусом 200 (OK),
     *         либо статусом 404 (Not Found) если счет не найден
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable UUID id, @RequestBody AccountUpdateDTO dto) {
        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    /**
     * Удаляет счет по указанному идентификатору
     *
     * @param id Идентификатор счета для удаления
     * @return ResponseEntity со статусом 204 (No Content) при успешном удалении,
     *         либо статусом 404 (Not Found) если счет не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
