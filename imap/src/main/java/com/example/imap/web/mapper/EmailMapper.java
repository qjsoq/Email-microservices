package com.example.imap.web.mapper;


import com.example.imap.domain.Email;
import com.example.imap.web.dto.EmailCreationDto;
import com.example.imap.web.dto.EmailDto;
import com.example.imap.web.dto.ReceivedEmail;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface EmailMapper {
    @Named("extractPersonal")
    static String extractPersonal(Address[] addresses) {
        return getAddressPart(addresses);
    }

    private static String getAddressPart(Address[] addresses) {
        if (addresses != null && addresses.length > 0 &&
                addresses[0] instanceof InternetAddress internetAddress) {
            return internetAddress.getPersonal();
        }
        return null;
    }

    Email toEmail(EmailCreationDto emailCreationDto);
    EmailDto toEmailDto(Email email);


    @Mapping(source = "from", target = "personal", qualifiedByName = "extractPersonal")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "receivedDate", target = "receivedDate")
    @Mapping(target = "msgnum", expression = "java(message.getMessageNumber())")
    @Mapping(target = "folder", expression = "java(message.getFolder().toString())")
    ReceivedEmail toReceivedEmail(Message message) throws MessagingException;
    @Mapping(target = "msgnum", expression = "java(email.getId() != null ? Math.toIntExact(email.getId()) : 0)")
    @Mapping(target = "folder", expression = "java(\"Sent\")")
    @Mapping(source = "senderEmail", target = "personal")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "sentAt", target = "receivedDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ReceivedEmail toReceivedEmail(Email email);
}

