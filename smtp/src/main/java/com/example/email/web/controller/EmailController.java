package com.example.email.web.controller;

import com.example.email.common.HttpResponse;
import com.example.email.common.HttpResponseEmailBuilder;
import com.example.email.domain.MailBox;
import com.example.email.service.EmailService;
import com.example.email.web.dto.EmailCreationDto;
import com.example.email.web.mapper.EmailMapper;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<HttpResponse> sendEmail(
            @Valid @RequestBody EmailCreationDto emailCreationDto, Principal principal)
            throws UnsupportedEncodingException, MessagingException {
        System.out.println(principal.getName());
        var email =
                emailService.sendEmail(emailMapper.toEmail(emailCreationDto), principal.getName());
        return ResponseEntity.ok(new HttpResponseEmailBuilder().build());
    }

    @PostMapping("/config")
    public ResponseEntity<MailBox> setEmailConfig(@RequestBody MailBox mailBox)
            throws MessagingException, IOException {
        return ResponseEntity.ok(emailService.addEmailConfiguration(mailBox));
    }

}
