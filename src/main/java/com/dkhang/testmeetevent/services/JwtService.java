package com.dkhang.testmeetevent.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${meet.aud}")
    private String meetAud;

    @Value("${meet.iss}")
    private String meetIss;

    @Value("${meet.sub}")
    private String meetSub;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateMeetToken(Map<String, Object> extraClaims) {
        return buildMeetToken(extraClaims, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    private String buildMeetToken(Map<String, Object> extraClaims, long jwtExpiration) {
        return Jwts
                .builder()
                .header()
                .type("JWT")
                .and()
                .claims(extraClaims)
                .claim("aud", meetAud)
                .issuer(meetIss)
                .subject(meetSub)
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getMeetKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw e;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }

    private SecretKey getMeetKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
