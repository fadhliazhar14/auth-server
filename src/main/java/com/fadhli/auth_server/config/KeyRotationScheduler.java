package com.fadhli.auth_server.config;

import com.fadhli.auth_server.service.JwksService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class KeyRotationScheduler {
    private final JwksService jwksService;

    @Scheduled(
            fixedRateString = "${key.rotation.rate:2592000000}",
            initialDelayString = "${key.rotation.delay:2592000000}"
    )
    public void rotateKeys() {
        jwksService.addNewJwksKey();
    }
}
