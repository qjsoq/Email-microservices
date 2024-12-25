package com.example.user.web.controller;


import com.example.user.common.HttpResponse;
import com.example.user.service.UserService;
import com.example.user.web.dto.mailbox.MailBoxCreation;
import com.example.user.web.dto.mailbox.MailBoxDto;
import com.example.user.web.mapper.MailBoxMapper;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailBoxMapper mailBoxMapper;

    @GetMapping("/mailbox")
    public ResponseEntity<List<MailBoxDto>> getMailBoxes(Principal principal) {
        var mailBoxes = userService.getMailBoxes(principal.getName());
        return ResponseEntity.ok(mailBoxes);
    }

    @PostMapping("/add-account")
    public ResponseEntity<HttpResponse> addAccount(
            @Valid @RequestBody MailBoxCreation mailBoxCreation,
            Principal principal) {
        var mailBox = userService.addAccount(mailBoxMapper.toMailBox(mailBoxCreation),
                principal.getName());
        return ResponseEntity.created(URI.create("")).body(HttpResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .code(201)
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("new-email-box", mailBoxMapper.toDto(mailBox)))
                .message("Email box was added")
                .path("/api/v1/users/add-account/{login}")
                .build());
    }

    @GetMapping("")
    public String handleGoogleCallback(@RequestParam("code") String authorizationCode)
            throws URISyntaxException, IOException {
        System.out.println("Authorization code: " + authorizationCode);
        return "Authorization code: " + authorizationCode;
    }
}
