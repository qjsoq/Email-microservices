package com.example.email.common;

import com.example.email.util.EmailConfiguration;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailProperties {
    private final EmailConfiguration ukrNetConfig = EmailConfiguration.UKRNET;
    private final EmailConfiguration gmailConfig = EmailConfiguration.GMAIL;
    @Bean(name = "gmail-properties")
    public Properties GetGmailProperties() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", gmailConfig.getPort());
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", gmailConfig.isUseTls());
        return props;
    }
    @Bean(name = "ukr-properties")
    public Properties getUkrNetProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", ukrNetConfig.getHost());
        props.put("mail.smtp.port", ukrNetConfig.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", ukrNetConfig.getPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        return props;
    }
}
