package com.example.email.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
    String jwtSecret;
    @Value("${jwt.issuer}")
    String jwtIssuer;

    public Optional<DecodedJWT> decodedJwt(String token) throws JWTVerificationException {
        return Optional.of(JWT.require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(jwtIssuer)
                .build()
                .verify(token));
    }
}
