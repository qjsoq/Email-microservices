package com.example.imap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ImapApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImapApplication.class, args);
    }

}
