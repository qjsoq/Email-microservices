package com.example.user.service.impl;


import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.user.client.ImapClient;
import com.example.user.client.SmtpClient;
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

import com.example.user.web.dto.mailbox.MailBoxDto;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SmtpClient smtpClient;
    private final ImapClient imapClient;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User saveUser(User user) throws IOException {
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
    public List<MailBoxDto> getMailBoxes(String login) {
        String decodedJWT = jwtTokenProvider.generateToken(userRepository.findByLoginIgnoreCase(login).get());
        String authorizationHeader = "Bearer " + decodedJWT;
        return imapClient.getMailBoxes(authorizationHeader);
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
            return smtpClient.addAccount(mailBox);
        } catch (Exception e) {
            String errorBody = e.getMessage();
            throw new ServiceException(HttpStatus.BAD_REQUEST, errorBody);
        }
    }
}
