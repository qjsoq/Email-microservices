package com.example.user.service.impl;


import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.user.domain.MailBox;
import com.example.user.domain.User;
import com.example.user.exception.InvalidPasswordException;
import com.example.user.exception.InvalidTokenException;
import com.example.user.exception.ServiceException;
import com.example.user.exception.UserAlreadyExistsException;
import com.example.user.exception.UserNotFoundException;
import com.example.user.repository.UserRepository;
import com.example.user.security.JwtTokenProvider;
import com.example.user.service.UserService;
import com.example.user.web.dto.ErrorResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;


    //private final EmailService emailService;
    //private final MailBoxRepository mailBoxRepository;

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        user.setEnable(false);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }
    @Override
    @Transactional
    public Optional<DecodedJWT> signIn(String login, String password) {
        var user = userRepository.findByLoginIgnoreCase(login).orElseThrow(() -> new UserNotFoundException(
                "User with login %s doesn`t exist".formatted(login)
        ));
        if (!encoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        var token = jwtTokenProvider.generateToken(user);
        return jwtTokenProvider.decodedJwt(token);
    }

    @Override
    public List<MailBox> getMailBoxes(String login) {
        String url = "http://imap/api/v1/read/mailbox";
        String decodedJWT = jwtTokenProvider.generateToken(userRepository.findByLoginIgnoreCase(login).get());
        return webClientBuilder.build()
                .get()
                .uri(url)
                .header("Authorization", "Bearer " + decodedJWT) // Add token to Authorization header
                .retrieve()
                .bodyToFlux(MailBox.class) // Deserialize response to Flux<MailBox>
                .collectList() // Convert Flux<MailBox> to List<MailBox>
                .block(); // Block to wait for the result (avoid in reactive programming unless necessary)
    }

    @Override
    public User validateToken(String token) {
        User user = null;
        try {
            Optional<DecodedJWT> decodedJwt = jwtTokenProvider.decodedJwt(token);
            if (decodedJwt.isPresent()) {
                String email = jwtTokenProvider.getLoginFromToken(token);
                user =  userRepository.findByLoginIgnoreCase(email).orElseThrow(() -> new UserNotFoundException("User not found"));
            }
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Token is Invalid");
        }
        return user;
    }


    @Override
    public MailBox addAccount(MailBox mailBox, String login) {
        User user = userRepository.findByLoginIgnoreCase(login).orElseThrow(() -> new UserNotFoundException("User not found"));
        mailBox.setUser(user);
        try {
            return webClientBuilder.build().post()
                    .uri("http://smtp-service/api/v1/email/config")
                    .bodyValue(mailBox)
                    .retrieve()
                    .bodyToMono(MailBox.class)
                    .block();
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString(); // The response body from smtp-service
            throw new ServiceException((HttpStatus) e.getStatusCode(), errorBody);
        }
    }
}
