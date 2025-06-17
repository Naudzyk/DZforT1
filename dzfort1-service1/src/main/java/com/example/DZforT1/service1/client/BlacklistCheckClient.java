package com.example.DZforT1.service1.client;

import com.example.DZforT1.core.DTO.BlacklistRequestDTO;
import com.example.DZforT1.core.DTO.BlacklistResponseDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "dzfort1-service2", url = "http://localhost:8081/blacklist")
public interface BlacklistCheckClient {

    @PostMapping("/check")
    ResponseEntity<BlacklistResponseDTO> checkClient(
        @RequestHeader("Authorization") String auth,
        @RequestBody BlacklistRequestDTO dto
    );
}
