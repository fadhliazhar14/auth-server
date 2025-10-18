package com.fadhli.auth_server.config;

import com.fadhli.auth_server.service.JwksService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyRotationScheduler {
    private final JwksService jwksService;

    @Scheduled(fixedRate = 30L * 24 * 60 * 60 * 1000)
    public void rotateKeys() {
        jwksService.addNewJwksKey();
    }
}
