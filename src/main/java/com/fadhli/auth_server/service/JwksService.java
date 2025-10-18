package com.fadhli.auth_server.service;

import com.fadhli.auth_server.entity.JwksKey;
import com.fadhli.auth_server.repository.JwksKeyRepository;
import com.fadhli.auth_server.util.RsaKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwksService {
    private final JwksKeyRepository jwksKeyRepository;

    public JwksKey findActiveJwksKey() {
        // First, try to find an active key with valid expiry
        Optional<JwksKey> activeKey = jwksKeyRepository.findActiveJwksKeyWithValidExpiry();

        return activeKey.orElseGet(this::addNewJwksKey);
    }

    public List<JwksKey> findAllActiveJwksKeys() {
        return jwksKeyRepository.findActiveJwksKeys();
    }

    public JwksKey findByKid(String kid) {
        return jwksKeyRepository.findByKid(kid)
                .orElseThrow(() -> new RuntimeException("JWK with key id: " + kid + " not found"));
    }

    @Transactional
    public PublicKey getPublicKeyByKid(String kid) {
        JwksKey jwksKey = findByKid(kid);
        return RsaKeyUtil.decodePublicKeyFromNAndE(jwksKey.getPublicKeyN(), jwksKey.getPublicKeyE());
    }

    @Transactional
    public JwksKey addNewJwksKey() {
        // Generate new RSA key pair
        KeyPair keyPair = RsaKeyUtil.generateRsaKey();

        // Extract modulus and exponent from public key
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

        String modulus = base64UrlEncodeWithoutLeadingZero(rsaPublicKey.getModulus());
        String exponent = base64UrlEncodeWithoutLeadingZero(rsaPublicKey.getPublicExponent());

        // Encoded private key in PEM format
        String encodedPrivateKey = RsaKeyUtil.encodePrivateKey(keyPair.getPrivate());

        // Create entity
        JwksKey jwksKey = new JwksKey();
        jwksKey.setKid("key-" + System.currentTimeMillis());
        jwksKey.setKty("RSA");
        jwksKey.setAlg("RS256");
        jwksKey.setKey_usage("sig");
        jwksKey.setPublicKeyN(modulus);
        jwksKey.setPublicKeyE(exponent);
        jwksKey.setPrivateKeyPem(encodedPrivateKey);
        jwksKey.setIsActive(true);
        jwksKey.setExpiresAt(Instant.now().plusSeconds(30 * 24 * 60 * 60)); // 30 days expiry

        // Deactivate any existing active keys
        deactivateAllActiveKeys();

        // Save to database
        return  jwksKeyRepository.save(jwksKey);
    }

    @Transactional
    public PublicKey findActivePublicKey() {
        JwksKey activeKey = findActiveJwksKey();

        return RsaKeyUtil.decodePublicKeyFromNAndE(activeKey.getPublicKeyN(), activeKey.getPublicKeyE());
    }

    @Transactional
    public PrivateKey findActivePrivateKey() {
        JwksKey activeKey = findActiveJwksKey();

        return RsaKeyUtil.decodePrivateKey(activeKey.getPrivateKeyPem());
    }

    @Transactional
    private void deactivateAllActiveKeys() {
        jwksKeyRepository.deactivateAllActiveKeys();
    }

    private static String base64UrlEncodeWithoutLeadingZero(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0x00) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
