package com.example.email.web.controller;

import com.example.email.common.HttpResponse;
import com.example.email.common.HttpResponseEmailBuilder;
import com.example.email.domain.MailBox;
import com.example.email.service.EmailSenderTemplate;
import com.example.email.service.EmailService;
import com.example.email.service.impl.AwsS3StorageService;
import com.example.email.web.dto.EmailCreationDto;
import com.example.email.web.mapper.EmailMapper;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final EmailSenderTemplate emailSenderTemplate;
    private final EmailMapper emailMapper;

    @PostMapping
    public ResponseEntity<HttpResponse> sendEmail(
            @RequestPart(value ="file", required = false) MultipartFile file,
            @RequestPart(value = "emailCreationDto") EmailCreationDto emailCreationDto,
            Principal principal) throws IOException, MessagingException {
                emailSenderTemplate.sendEmailTemplate(emailMapper.toEmail(emailCreationDto), principal.getName(),
                        file);
        return ResponseEntity.ok(new HttpResponseEmailBuilder().build());
    }


    @PostMapping("/config")
    public ResponseEntity<MailBox> setEmailConfig(@RequestBody MailBox mailBox)
            throws MessagingException, IOException {
        return ResponseEntity.ok(emailService.addEmailConfiguration(mailBox));
    }

}
