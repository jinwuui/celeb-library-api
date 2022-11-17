package com.eunbinlib.api.auth.utils;


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

import static com.eunbinlib.api.auth.data.JwtProperties.HEADER_STRING;
import static com.eunbinlib.api.auth.data.JwtProperties.TOKEN_PREFIX;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    private static final String TOKEN_TYPE = "tokenType";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-time}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private Long refreshTokenExpirationTime;

    public String createAccessToken(String username) {

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration)
                .setIssuedAt(now)
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .claim(TOKEN_TYPE, ACCESS_TOKEN)
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
                .claim(TOKEN_TYPE, REFRESH_TOKEN)
                .compact();
    }

    public Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(header.replace(TOKEN_PREFIX, ""));
    }

    public Claims verifyAccessToken(String token) {
        return Jwts.parser()
                .require(TOKEN_TYPE, ACCESS_TOKEN)
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims verifyRefreshToken(String token) {
        return Jwts.parser()
                .require(TOKEN_TYPE, REFRESH_TOKEN)
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
