package com.example.email.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EmailConfiguration {

    GMAIL("gmail", "smtp.gmail.com", "imap.gmail.com", 587, false, true),
    OUTLOOK("outlook", "smtp-mail.outlook.com", "imap-outlook.com", 587, false, true),
    UKRNET("ukr", "smtp.ukr.net", "imap.ukr.net", 465, false, true),
    YAHOO("yahoo", "smtp.mail.yahoo.com", "imap.mail.yahoo.com", 465, true, false);

    private final String domain;
    private final String host;
    private final String imapHost;
    private final int port;
    private final boolean useSsl;
    private final boolean useTls;

    public String getDomainName() {
        return domain;
    }

    public String getHost() {
        return host;
    }

    public String getImapHost() {
        return imapHost;
    }

    public int getPort() {
        return port;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public boolean isUseTls() {
        return useTls;
    }
}
