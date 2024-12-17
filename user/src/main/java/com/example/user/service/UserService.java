package com.example.user.service;


import com.example.user.domain.MailBox;
import com.example.user.domain.User;

public interface UserService {
    User saveUser(User user);


    MailBox addAccount(MailBox MailBox, String login);

}
