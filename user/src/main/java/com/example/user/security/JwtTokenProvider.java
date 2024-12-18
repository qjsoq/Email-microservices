package com.example.user.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.user.domain.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer(jwtIssuer)
                .withSubject(user.getLogin())
                .withExpiresAt(setTime())
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public Optional<DecodedJWT> decodedJwt(String token) throws JWTVerificationException {
        return Optional.of(JWT.require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(jwtIssuer)
                .build()
                .verify(token));
    }

    public String getLoginFromToken(String token) {
        return JWT.require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(jwtIssuer)
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant setTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        Instant instant = dateTime.atZone(ZoneId.of("Europe/Paris")).toInstant();
        return instant.plus(7, ChronoUnit.DAYS);
    }

}

