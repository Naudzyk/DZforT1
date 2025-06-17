package com.example.DZforT1.service2.controller;

import com.example.DZforT1.core.DTO.BlacklistRequestDTO;
import com.example.DZforT1.core.DTO.BlacklistResponseDTO;
import com.example.DZforT1.service2.service.Impl.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping("/check")
    public ResponseEntity<BlacklistResponseDTO> checkClient(@RequestBody BlacklistRequestDTO dto) {
        boolean isBlacklisted = blacklistService.isClientBlacklisted(dto.clientId(), dto.accountId());
        return ResponseEntity.ok(new BlacklistResponseDTO(dto.clientId(), dto.accountId(), isBlacklisted));
    }
}
