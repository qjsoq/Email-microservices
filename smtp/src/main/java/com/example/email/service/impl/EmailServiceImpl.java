package com.example.email.service.impl;

import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import com.example.email.exception.InvalidEmailDomainException;
import com.example.email.exception.MailBoxAlreadyExistsException;
import com.example.email.exception.MailboxNotFoundException;
import com.example.email.exception.StrategyNotFoundException;
import com.example.email.repository.MailBoxRepository;
import com.example.email.service.EmailService;
import com.example.email.service.SendStrategy;
import com.example.email.util.EmailConfiguration;
import com.example.email.web.dto.DetailedReceivedEmail;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MailBoxRepository mailBoxRepository;
    private final List<SendStrategy> listOfStrategies;
    private SendStrategy sendStrategy;

    @Override
    public Email sendEmail(Email email, String login) throws MessagingException, UnsupportedEncodingException {
        var mailBox = mailBoxRepository.findByEmailAddressAndUserLogin(email.getSenderEmail(), login)
                .orElseThrow(()-> new MailboxNotFoundException(String.format("Mail box not found %s", email.getSenderEmail())));

        specifyStrategy(email.getSenderEmail(), login);
        return sendStrategy.sendWithStrategyEmail(email, mailBox);
    }
    @Override
    public MailBox addEmailConfiguration(MailBox mailBox) {
        checkIfMailBoxExist(mailBox.getEmailAddress(), mailBox.getUser().getLogin());
        String domainPart = getEmailDomain(mailBox.getEmailAddress());
        Optional<EmailConfiguration> appropriateConfig =
                Arrays.stream(EmailConfiguration.values())
                        .filter(config -> config.getDomainName().equalsIgnoreCase(domainPart))
                        .findFirst();
        if (appropriateConfig.isPresent()) {
            System.out.println("I am here 1");
            mailBox.setEmailConfiguration(appropriateConfig.get());
            mailBoxRepository.save(mailBox);
            return mailBox;
        }
        throw new InvalidEmailDomainException(String.format("the provided domainpart %s is not supported", domainPart));
    }


    private void checkIfMailBoxExist(String emailAddress, String login) {
        var mailbox = mailBoxRepository.findByEmailAddressAndUserLogin(emailAddress, login);
        if (mailbox.isPresent()){
            throw new MailBoxAlreadyExistsException(String.format("Email box %s is already used", emailAddress));
        }

    }


    private String getEmailDomain(String emailAddress) {
        Pattern pattern = Pattern.compile("(?<=@)[^.]+(?=\\.)");
        Matcher matcher = pattern.matcher(emailAddress);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new IllegalArgumentException("Invalid email address format: " + emailAddress);
        }
    }

    public void specifyStrategy(String senderEmail, String login) {
        MailBox mailBox = mailBoxRepository.findByEmailAddressAndUserLogin(senderEmail, login)
                .orElseThrow(() -> new MailboxNotFoundException("Sender email not found: " + senderEmail));

        String strategyBeanName = mailBox.getEmailConfiguration().getDomainName();

        sendStrategy = listOfStrategies.stream()
                .filter(strategy -> strategy.getClass().isAnnotationPresent(Service.class) &&
                        strategy.getClass().getAnnotation(Service.class).value()
                                .equalsIgnoreCase(strategyBeanName))
                .findFirst()
                .orElseThrow(() -> new StrategyNotFoundException(
                        "No SendStrategy found for: " + strategyBeanName));
    }

    public String getTextFromMimeMultipart2(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {

                result = result + "\n" + bodyPart.getContent();
                break;

            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + html; //org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart2((MimeMultipart) bodyPart.getContent());

            }
        }
        return result;
    }

}
