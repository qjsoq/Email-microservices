package com.example.imap.domain;

import com.example.imap.config.MailBoxListener;
import com.example.imap.util.EmailConfiguration;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
