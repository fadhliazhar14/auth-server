package com.fadhli.auth_server.controller;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.keypair.JwkResponseDto;
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

    @GetMapping
    public ResponseEntity<ApiResponse<JwkResponseDto>> getPublicKey() {
        List<JwksKey> activeKeys = jwksService.findAllActiveJwksKeys();
        List<JwkResponseDto.JwkKey> jwkKeys = activeKeys.stream()
                .map(key -> {
                    JwkResponseDto.JwkKey jwkKey = new JwkResponseDto.JwkKey();
                    jwkKey.setKeyType(key.getKty());
                    jwkKey.setPublicKeyUse(key.getKey_usage());
                    jwkKey.setKeyId(key.getKid());
                    jwkKey.setModulus(key.getPublicKeyN());
                    jwkKey.setExponent(key.getPublicKeyE());
                    jwkKey.setAlgorithm(key.getAlg());

                    return jwkKey;
                }).toList();
        JwkResponseDto jwks = new JwkResponseDto(jwkKeys);
        ApiResponse<JwkResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, jwks);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<JwkResponseDto>> getJwk() {
        JwksKey latestKey = jwksService.findActiveJwksKey();

        JwkResponseDto.JwkKey jwkKey = new JwkResponseDto.JwkKey();
        jwkKey.setKeyType(latestKey.getKty());
        jwkKey.setPublicKeyUse(latestKey.getKey_usage());
        jwkKey.setKeyId(latestKey.getKid());
        jwkKey.setModulus(latestKey.getPublicKeyN());
        jwkKey.setExponent(latestKey.getPublicKeyE());
        jwkKey.setAlgorithm(latestKey.getAlg());

        JwkResponseDto jwks = new JwkResponseDto(List.of(jwkKey));
        ApiResponse<JwkResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, jwks);

        return ResponseEntity.ok(response);

    }
}
