package com.fadhli.auth_server.dto.keypair;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwkResponseDto {
    @JsonProperty("keys")
    private List<JwkKey> keys;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwkKey {
        @JsonProperty("kty")
        private String keyType;

        @JsonProperty("use")
        private String publicKeyUse;

        @JsonProperty("kid")
        private String keyId;

        @JsonProperty("n")
        private String modulus;

        @JsonProperty("e")
        private String exponent;

        @JsonProperty("alg")
        private String algorithm;
    }
}
