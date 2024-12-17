package com.example.email.web.dto;

import java.util.Date;
import lombok.Data;

@Data
public class ReceivedEmail {
    private int msgnum;
    private String folder;
    private String personal;
    private String subject;
    private Date receivedDate;

}
