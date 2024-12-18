package com.example.email.web.controller;

import com.example.email.domain.HttpResponse;
import com.example.email.domain.MailBox;
import com.example.email.service.EmailService;
import com.example.email.web.dto.EmailCreationDto;
import com.example.email.web.mapper.EmailMapper;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final EmailMapper emailMapper;

    @PostMapping
    public ResponseEntity<HttpResponse> sendEmail(@RequestBody EmailCreationDto emailCreationDto, Principal principal)
            throws UnsupportedEncodingException, MessagingException {
        System.out.println(principal.getName());
        var email = emailService.sendEmail(emailMapper.toEmail(emailCreationDto), principal.getName());
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .data(Map.of("email", emailMapper.toDto(email)))
                .path("/api/v1/emails")
                .timeStamp(LocalDateTime.now().toString())
                .build());
    }

    @PostMapping("/config")
    public ResponseEntity<MailBox> setEmailConfig(@RequestBody MailBox mailBox){
        return ResponseEntity.ok(emailService.addEmailConfiguration(mailBox));
    }

}
