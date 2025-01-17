package com.example.imap.repository;

import com.example.imap.domain.Email;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long>{
    List<Email> findByUserLogin(String login);
}
