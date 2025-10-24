package com.fadhli.auth_server.util;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKeyUtil {
    public static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }

    public static String encodePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String encodePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey decodePublicKey(String encodedPublicKey) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(encodedPublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding public key", e);
        }
    }

    public static PrivateKey decodePrivateKey(String encodedPrivateKey) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(encodedPrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding private key", e);
        }
    }

    public static PublicKey decodePublicKeyFromNAndE(String encodedModulus, String encodedExponent) {
        try {
            // Decode the Base64URL encoded values back to bytes
            byte[] modulusBytes = Base64.getUrlDecoder().decode(encodedModulus);
            byte[] exponentBytes = Base64.getUrlDecoder().decode(encodedExponent);

            // Convert bytes to BigInteger
            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);

            // Create RSA public key specification
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);

            // Generate the public key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error constructing public key from n and e", e);
        }
    }
}
