package com.example.email.domain;

import com.example.email.config.MailBoxListener;
import com.example.email.util.EmailConfiguration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Setter
@Getter
@EntityListeners(MailBoxListener.class)
public class MailBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String emailAddress;
    private String accessSmtp;
    private String refreshToken;
    @Enumerated(value = EnumType.STRING)
    private EmailConfiguration emailConfiguration;
}
