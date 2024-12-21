package com.example.imap.web.controller;


import com.example.imap.domain.HttpResponse;
import com.example.imap.domain.MailBox;
import com.example.imap.service.ImapService;
import com.example.imap.web.dto.MailBoxDto;
import com.example.imap.web.mapper.EmailMapper;
import jakarta.mail.MessagingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/read")
@RequiredArgsConstructor
public class ImapController {
    private final EmailMapper emailMapper;
    private final ImapService imapService;

    @PostMapping("/{account}")
    public ResponseEntity<HttpResponse> readEmails(@PathVariable String account,
                                                   @RequestBody Map<String, String> folderNameMap, Principal principal)
            throws Exception {
        String folderName = folderNameMap.get("folderName");
        var messages = imapService.getEmails(account, folderName, principal.getName());
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

    @PostMapping("/{account}/{msgnum}")
    public ResponseEntity<HttpResponse> getSpecificEmail(@PathVariable String account,
                                                         @RequestBody Map<String, String> folderNameMap,
                                                         @PathVariable int msgnum, Principal principal)
            throws Exception {
        String folderName = folderNameMap.get("folderName");
        var email = imapService.getSpecificEmail(account, folderName, msgnum, principal.getName());
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .path(String.format("/api/v1/read/%s/%s/%s", account, folderName, msgnum))
                .data(Map.of("email", email))
                .timeStamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/mailbox")
    public ResponseEntity<List<MailBoxDto>> getMailBoxes(Principal principal) {
        return ResponseEntity.ok(imapService.getMailBoxes(principal.getName()));
    }
}
