package com.example.imap.common;

import com.example.imap.util.EmailConfiguration;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailProperties {
    private final EmailConfiguration ukrNetConfig = EmailConfiguration.UKRNET;
    private final EmailConfiguration gmailConfig = EmailConfiguration.GMAIL;

    @Bean(name = "ukr-imap-properties")
    public Properties getImapUkrNetProperties() {
        Properties props = getDefaultProperties(ukrNetConfig);
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.auth", "true");
        return props;
    }
    @Bean(name = "gmail-imap-properties")
    public Properties getImapGmailProperties() {
        Properties props = getDefaultProperties(gmailConfig);
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        return props;
    }

    private Properties getDefaultProperties(EmailConfiguration properties) {
        Properties props = new Properties();
        props.put("mail.imap.host", properties.getImapHost());
        props.put("mail.imaps.ssl.trust", properties.getImapHost());
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", properties.isUseTls());
        props.put("mail.imaps.connectiontimeout", "10000");
        props.put("mail.imaps.timeout", "10000");
        return props;
    }
}
