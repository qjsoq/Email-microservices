package com.example.user.web.mapper;


import com.example.user.domain.MailBox;
import com.example.user.web.dto.mailbox.MailBoxCreation;
import com.example.user.web.dto.mailbox.MailBoxDto;
import org.mapstruct.Mapper;

@Mapper
public interface MailBoxMapper {
    MailBox toMailBox(MailBoxCreation mailBoxCreation);
    MailBoxDto toDto(MailBox mailBox);
}
