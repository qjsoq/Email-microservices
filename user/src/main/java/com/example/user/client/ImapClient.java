package com.example.user.client;

import com.example.user.domain.MailBox;
import java.util.List;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

public interface ImapClient {
    @GetExchange("/api/v1/read/mailbox")
    public List<MailBox> getMailBoxes(@RequestHeader("Authorization") String authorization);
}
