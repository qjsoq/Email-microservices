package com.example.email.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table
@Entity
public class Email {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private String imageKey;
    private String senderEmail;
    private String recipientEmail;
    private String body;
    private LocalDateTime sentAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
