package com.example.imap.domain;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailAttachment {
    private String fileName;
    private String mimeType;
    private String attachmentId; // Unique identifier for this attachment

    // Getters and setters
}



