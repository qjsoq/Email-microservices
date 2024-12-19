package com.example.imap.web.mapper;


import com.example.imap.domain.Email;
import com.example.imap.web.dto.EmailDto;
import com.example.imap.web.dto.ReceivedEmail;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
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


    EmailDto toDto(Email email);

    @Mapping(source = "from", target = "personal", qualifiedByName = "extractPersonal")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "receivedDate", target = "receivedDate")
    @Mapping(target = "msgnum", expression = "java(message.getMessageNumber())")
    @Mapping(target = "folder", expression = "java(message.getFolder().toString())")
    ReceivedEmail toReceivedEmail(Message message) throws MessagingException;
}
