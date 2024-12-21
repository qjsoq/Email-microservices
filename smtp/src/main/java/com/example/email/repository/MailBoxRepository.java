package com.example.email.repository;

import com.example.email.domain.MailBox;
import com.example.email.util.EmailConfiguration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailBoxRepository extends JpaRepository<MailBox, Integer> {
    Optional<MailBox> findByEmailAddressAndUserLogin(String emailAddress, String login);
    List<MailBox> findByEmailConfiguration(EmailConfiguration emailConfiguration);

}
