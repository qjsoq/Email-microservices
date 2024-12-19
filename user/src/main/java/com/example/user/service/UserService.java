package com.example.user.service;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.user.domain.MailBox;
import com.example.user.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);


    MailBox addAccount(MailBox MailBox, String login);
    Optional<DecodedJWT> signIn(String login, String password);
    List<MailBox> getMailBoxes(String login);
    User validateToken(String token);
}
