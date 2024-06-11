package com.example.gpq.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "lY6TOpiotR2OchjbY5RHeiPKLZOnSRygNsCxZBWyxA8/zIN+VdFcd7XpWo7rdqb+Q7JQQ/K19Og0Hwl7+T3MyqGDvzKZr08n8hd+OV395P0NYIFBl4J5Ov25ZZKFd3ng85y1L6dszuEQIdAm959qL9mD1dY2QX/soVvXSg8NouxkqggqbrLli1cavRNY4XuvGF0NqaZV0A5H2LxgzLZUiNTBfZYEb2ZQpSAfrWtXpdgLklapVGabDu0Rg366MpBAQM+Y0QtGDNAckYMBBWRkLMZAnjccwKnTPq5gyFt1o7xTDE0GL3UlspbxcAQyRzXQ3yhg9LiQXzg7I/75yOGzXhK+t5Qv39AuBdeGoaxhSNE=\n";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }



    public String generateTokenFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(refreshToken).getBody();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.getSubject())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(365, ChronoUnit.DAYS))) // Durée d'expiration d'un an
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority()); // Ajouter le rôle aux revendications
        return createToken(claims, userDetails.getUsername(), Duration.ofDays(365)); // Durée d'expiration d'un an
    }

    public String createToken(Map<String, Object> claims, String subject, Duration duration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(duration)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}