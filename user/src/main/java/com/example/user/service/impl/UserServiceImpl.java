package com.example.user.service.impl;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.user.domain.MailBox;
import com.example.user.domain.User;
import com.example.user.repository.UserRepository;
import com.example.user.security.JwtTokenProvider;
import com.example.user.service.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
            throw new RuntimeException("Email already exists");
        }
        user.setEnable(false);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }
    @Override
    @Transactional
    public Optional<DecodedJWT> signIn(String login, String password) {
        var user = userRepository.findByLoginIgnoreCase(login).orElseThrow(() -> new RuntimeException(
                "User with email %s doesn`t exist".formatted(login)
        ));
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        var token = jwtTokenProvider.generateToken(user);
        return jwtTokenProvider.decodedJwt(token);
    }

    @Override
    public List<MailBox> getMailBoxes(String login) {
        String url = String.format( "http://imap/api/v1/read/%s/mailbox", login);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(MailBox.class) // Deserialize response to Flux<MailBox>
                .collectList() // Convert Flux<MailBox> to List<MailBox>
                .block(); // Block to wait for the result (avoid in reactive programming unless necessary)
    }

    @Override
    public User validateToken(String presentToken) {
        String login = jwtTokenProvider.getLoginFromToken(presentToken);
        return userRepository.findByLoginIgnoreCase(login).orElseThrow(RuntimeException::new);
    }


    @Override
    public MailBox addAccount(MailBox mailBox, String login) {
        User user = userRepository.findByLoginIgnoreCase(login).orElseThrow(RuntimeException::new);
        mailBox.setUser(user);
        return webClientBuilder.build().post()
                .uri("http://smtp-service/api/v1/email/config")
                .bodyValue(mailBox)
                .retrieve()
                .bodyToMono(MailBox.class)
                .block();
    }
}
