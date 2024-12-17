package com.example.user.service.impl;


import com.example.user.domain.MailBox;
import com.example.user.domain.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final WebClient webClient;
    //private final EmailService emailService;
    //private final MailBoxRepository mailBoxRepository;

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new RuntimeException("Email already exists");
        }
        user.setEnable(false);
        user.setPassword(user.getPassword());
        userRepository.save(user);
        return user;
    }



    @Override
    public MailBox addAccount(MailBox mailBox, String login) {
        var user = userRepository.findByLoginIgnoreCase(login);
        mailBox.setUser(user);
        user.getUserEmails().add(mailBox);
        userRepository.save(user);
        return webClient.post()
                .uri("http://localhost:8083//api/v1/send/config")
                .bodyValue(mailBox)
                .retrieve()
                .bodyToMono(MailBox.class)
                .block();
    }
}
