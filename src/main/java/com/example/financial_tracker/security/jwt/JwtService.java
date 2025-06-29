package com.example.financial_tracker.security.jwt;

import com.example.financial_tracker.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long jwtExpirationInMs;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(User user) {
    return Jwts.builder()
      .setSubject(user.getEmail())
      .claim("role", user.getRole().name())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, User user) {
    final String username = extractUsername(token);
    return username.equals(user.getEmail()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return parseClaims(token).getExpiration().before(new Date());
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public String extractRole(String token) {
    return parseClaims(token).get("role", String.class);
  }

}
