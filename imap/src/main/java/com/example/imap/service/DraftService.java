package com.example.imap.service;

import com.example.imap.domain.Email;

public interface DraftService {
    void saveEmailAsDraft(Email email, String login) throws Exception;
}
