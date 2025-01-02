package com.example.imap.web.controller;


import com.example.imap.common.HttpResponse;
import com.example.imap.service.ImapService;
import com.example.imap.web.dto.EmailDto;
import com.example.imap.web.dto.MailBoxDto;
import com.example.imap.web.mapper.EmailMapper;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/read")
@RequiredArgsConstructor
public class ImapController {
    private final EmailMapper emailMapper;
    private final ImapService imapService;

        @PostMapping("/{account}/num-of-mails/{numOfmails}")
    public ResponseEntity<HttpResponse> readEmails(@PathVariable String account, @PathVariable int numOfmails,
                                                   @RequestBody Map<String, String> folderNameMap,
                                                   Principal principal)
            throws Exception {
        String folderName = folderNameMap.get("folderName");
        var messages = imapService.getEmails(account, folderName, principal.getName(), numOfmails);
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .timeStamp(LocalDateTime.now().toString())
                .path(String.format("/api/v1/read/%s/%s", account, folderName))
                .data(Map.of("List of emails", messages))
                .build());
    }

    @PostMapping("/{account}/{msgnum}")
    public ResponseEntity<HttpResponse> getSpecificEmail(@PathVariable String account,
                                                         @RequestBody
                                                         Map<String, String> folderNameMap,
                                                         @PathVariable int msgnum,
                                                         Principal principal)
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
    @PostMapping("/{account}/{msgnum}/attachments/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(
            @RequestBody Map<String, String> folderNameMap,
            @PathVariable String account,
            @PathVariable int msgnum,
            @PathVariable String attachmentId,
            Principal principal) {
        try {
            String folderName = folderNameMap.get("folderName");
            Message message = imapService.getEmail(account, folderName, msgnum, principal.getName());
            if (message.getContent() instanceof MimeMultipart mimeMultipart) {
                BodyPart bodyPart =
                        mimeMultipart.getBodyPart(Integer.parseInt(attachmentId.split("-")[1]));
                if (bodyPart != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bodyPart.getInputStream().transferTo(outputStream);
                    byte[] data = outputStream.toByteArray();

                    HttpHeaders headers = new HttpHeaders();
                    headers.set(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + bodyPart.getFileName() + "\"");
                    headers.setContentType(org.springframework.http.MediaType.parseMediaType(
                            bodyPart.getContentType()));

                    return new ResponseEntity<>(data, headers, HttpStatus.OK);
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<EmailDto>> getSavedMails(Principal principal) {
        return ResponseEntity.ok(imapService.getSavedEmails(principal.getName()).stream()
                .map(emailMapper::toEmailDto).toList());
    }
}

