package com.example.imap.web.controller;


import com.example.imap.domain.HttpResponse;
import com.example.imap.domain.MailBox;
import com.example.imap.service.ImapService;
import com.example.imap.web.mapper.EmailMapper;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/read")
@RequiredArgsConstructor
public class ImapController {
    private final EmailMapper emailMapper;
    private final ImapService imapService;

    @GetMapping("/{account}/{folderName}")
    public ResponseEntity<HttpResponse> readEmails(@PathVariable String account,
                                                   @PathVariable String folderName)
            throws Exception {
        var messages = imapService.getEmails(account, folderName);
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .timeStamp(LocalDateTime.now().toString())
                .path(String.format("/api/v1/read/%s/%s", account, folderName))
                .data(Map.of("List of emails", Arrays.stream(messages).map((temp) -> {
                    try {
                        return emailMapper.toReceivedEmail(temp);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList()))
                .build());
    }

    @GetMapping("/{account}/{folderName}/{msgnum}")
    public ResponseEntity<HttpResponse> getSpecificEmail(@PathVariable String account,
                                                         @PathVariable String folderName,
                                                         @PathVariable int msgnum)
            throws Exception {
        var email = imapService.getSpecificEmail(account, folderName, msgnum);
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .path(String.format("/api/v1/read/%s/%s/%s", account, folderName, msgnum))
                .data(Map.of("email", email))
                .timeStamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/{login}/mailbox")
    public ResponseEntity<List<MailBox>> getMailBoxes(@PathVariable String login) {
        return ResponseEntity.ok(imapService.getMailBoxes(login));
    }


}
