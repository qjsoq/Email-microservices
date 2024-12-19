package com.example.user.client;

import com.example.user.domain.MailBox;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "imap-service", url ="http://localhost:8081")
public interface ImapClient {
    @GetMapping("/api/v1/read/mailbox")
    public List<MailBox> getMailBoxes(@RequestHeader("Authorization") String authorization);
}
