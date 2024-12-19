package com.example.email.security;

import static org.springframework.util.StringUtils.hasText;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.email.client.UserClient;
import com.example.email.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver exceptionResolver;
    private final UserClient userClient;

    public JwtFilter(@Qualifier("handlerExceptionResolver")
                     HandlerExceptionResolver exceptionResolver, UserClient userClient) {
        this.exceptionResolver = exceptionResolver;
        this.userClient = userClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = retrieveTokenFromRequest(request);
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        String presentToken = token.get();
        try {
            User user = userClient.checkUser(presentToken);
            var authentication =
                    new UsernamePasswordAuthenticationToken(user, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JWTVerificationException e) {
            exceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);

    }

    private Optional<String> retrieveTokenFromRequest(HttpServletRequest httpServletRequest) {
        String header = httpServletRequest.getHeader("Authorization");
        if (hasText(header) && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }
        return Optional.empty();
    }
}
