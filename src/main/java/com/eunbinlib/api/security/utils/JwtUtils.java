package com.eunbinlib.api.security.utils;


import com.eunbinlib.api.security.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.eunbinlib.api.security.config.JwtProperties.HEADER_STRING;
import static com.eunbinlib.api.security.config.JwtProperties.TOKEN_PREFIX;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-time}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private Long refreshTokenExpirationTime;

    public String createAccessToken(CustomUserDetails customUserDetails) {

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String createRefreshToken(CustomUserDetails customUserDetails) {

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String createAccessToken(String username) {

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String createRefreshToken(String username) {

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(header.replace(TOKEN_PREFIX, ""));
    }

    public Claims verifyToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
