package com.fadhli.auth_server.service;

import com.fadhli.auth_server.entity.JwksKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private final JwksService jwksService;

    @Value("${token.issuer}")
    private String tokenIssuer;

    @Value("${jwt.expiration}")
    private Long expiration;

    @EventListener(ApplicationReadyEvent.class)
    public void initAfterStartup() {
        try {
            JwksKey key = jwksService.findActiveJwksKey();
            log.info("Active JWKS key loaded: {}", key.getKid());
        } catch (Exception e) {
            log.warn("Failed to initialize JWKS key, will be created when needed: {}", e.getMessage());
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, userDetails.getUsername(), userDetails.getAuthorities().toString());
    }

    private String createToken(Map<String, Object> claims, String subject, String roles) {
        JwksKey jwksKey = jwksService.findActiveJwksKey();
        PrivateKey signingKey = jwksService.findActivePrivateKey();

        return Jwts.builder()
                .setHeaderParam("kid", jwksKey.getKid())
                .setClaims(claims)
                .setIssuer(tokenIssuer)
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String extractUsername(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Parse the header first to get the kid
        String kid = getKidFromToken(token);
        
        PublicKey publicKey;
        if (kid != null) {
            // Use the specific key identified by kid for verification
            publicKey = jwksService.getPublicKeyByKid(kid);
        } else {
            // Fallback to active public key if no kid is present
            publicKey = jwksService.findActivePublicKey();
        }

        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getKidFromToken(String token) {
        // Extract header without verification to get kid
        String[] chunks = token.split("\\.");
        if (chunks.length >= 2) {
            String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));
            try {
                Map<String, Object> header = new ObjectMapper()
                        .readValue(headerJson, Map.class);
                return (String) header.get("kid");
            } catch (Exception e) {
                // If we can't parse the header, return null
                return null;
            }
        }

        return null;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);

        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
