package com.example.imap.repository;

import com.example.imap.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long>{
}
