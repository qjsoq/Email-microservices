package com.example.user.web.dto;

import java.util.Date;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;
    private String type;
    private String algorithm;
    private Date expires;
}
