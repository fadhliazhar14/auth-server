package com.fadhli.auth_server.controller;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.keypair.JwkResponseDto;
import com.fadhli.auth_server.dto.token.JwkMapper;
import com.fadhli.auth_server.entity.JwksKey;
import com.fadhli.auth_server.service.JwksService;
import com.fadhli.auth_server.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jwks")
@RequiredArgsConstructor
public class JwkController {
    private final JwksService jwksService;
    private final JwkMapper jwkMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<JwkResponseDto>> getJwk() {
        List<JwksKey> activeKeys = jwksService.findAllActiveJwksKeys();
        List<JwkResponseDto.JwkKey> jwkKeys = jwkMapper.toJwkKeyDtoList(activeKeys);
        JwkResponseDto jwks = new JwkResponseDto(jwkKeys);
        ApiResponse<JwkResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, jwks);

        return ResponseEntity.ok(response);
    }
}
