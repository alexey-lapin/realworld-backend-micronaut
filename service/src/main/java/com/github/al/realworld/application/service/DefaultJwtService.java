package com.github.al.realworld.application.service;

import com.github.al.realworld.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import java.security.Key;

@Singleton
public class DefaultJwtService implements JwtService {

    private final Key key;

    public DefaultJwtService(@Value("${security.jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public String getToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .signWith(key)
                .compact();
    }
}
