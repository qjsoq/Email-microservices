package com.example.email.web.dto;

import java.util.Date;
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

}
