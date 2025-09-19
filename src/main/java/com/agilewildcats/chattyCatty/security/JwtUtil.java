package com.agilewildcats.chattyCatty.security;

import com.agilewildcats.chattyCatty.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs = 1000L * 60 * 60 * 24; // 24h

    public JwtUtil(Environment env) {
        String secret = env.getProperty("JWT_SECRET");
        if (secret == null || secret.length() < 16) {
            // fallback - but you should set JWT_SECRET in env for production
            secret = "internal-only-dev-secret-change-me-please!";
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, Set<Role> roles) {
        String rolesStr = roles.stream().map(Enum::name).collect(Collectors.joining(","));
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", rolesStr)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validate(String token) throws JwtException {
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

