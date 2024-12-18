package com.example.email.repository;

import com.example.email.domain.MailBox;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailBoxRepository extends JpaRepository<MailBox, Integer> {
    Optional<MailBox> findByEmailAddress(String email);
    Optional<MailBox> findByEmailAddressAndUserLogin(String emailAddress, String login);

}
