package com.fadhli.auth_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jwks_keys")
public class JwksKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kid;

    @Column(nullable = false)
    private String kty;

    @Column(nullable = false)
    private String alg;

    @Column
    private String key_usage;

    @Column(name = "public_key_n", nullable = false, columnDefinition = "TEXT")
    private String publicKeyN;

    @Column(name = "public_key_e", nullable = false, columnDefinition = "TEXT")
    private String publicKeyE;

    @Column(name = "private_key_pem", nullable = false, columnDefinition = "TEXT")
    private String privateKeyPem;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
}
