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
        private String kty;

        @JsonProperty("use")
        private String key_usage;

        @JsonProperty("kid")
        private String kid;

        @JsonProperty("n")
        private String publicKeyN;

        @JsonProperty("e")
        private String publicKeyE;

        @JsonProperty("alg")
        private String alg;
    }
}
