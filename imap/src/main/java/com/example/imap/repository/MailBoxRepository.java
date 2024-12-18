package com.example.imap.repository;

import com.example.imap.domain.MailBox;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailBoxRepository extends JpaRepository<MailBox, Integer> {
    Optional<MailBox> findByEmailAddress(String email);
    List<MailBox> findByUserLogin(String login);
}
