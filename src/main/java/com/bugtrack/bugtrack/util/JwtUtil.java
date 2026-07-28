package com.bugtrack.bugtrack.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey(){
        byte[] keyBytes= secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generatetoken(UserDetails userDetails, String role){
        Map<String, Object> claims= new HashMap<>();
        claims.put("role", role);
        
        return createtoken(claims, userDetails.getUsername());
    }

    private String createtoken(Map<String, Object> claims, String subject) {
        return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSigningKey()).compact();
    }
    
    public String extractemail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractrole(String token){
        return extractClaim(token, claims->claims.get("role", String.class));
    }

    public boolean validatetoken(String token, UserDetails userDetails){
        final String email= extractemail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpire(token));
    }

    private boolean isTokenExpire(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
