package com.example.imap.web.dto;

import com.example.imap.domain.EmailAttachment;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailedReceivedEmail {
    private String body;
    private String receiverEmail;
    private String senderEmail;
    private String subject;
    private Date receivedDate;
    private List<EmailAttachment> attachments;
}
