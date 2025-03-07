package com.example.email.service.impl;

import com.example.email.domain.Email;
import com.example.email.domain.MailBox;
import com.example.email.exception.InvalidEmailDomainException;
import com.example.email.exception.MailBoxAlreadyExistsException;
import com.example.email.exception.MailboxNotFoundException;
import com.example.email.exception.SendException;
import com.example.email.exception.StrategyNotFoundException;
import com.example.email.repository.EmailRepository;
import com.example.email.repository.MailBoxRepository;
import com.example.email.service.EmailService;
import com.example.email.service.SendStrategy;
import com.example.email.service.StorageService;
import com.example.email.util.EmailConfiguration;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private final MailBoxRepository mailBoxRepository;
    private final StorageService storageService;
    private final EmailRepository emailRepository;
    private final List<SendStrategy> listOfStrategies;
    @Value("${google-client.id}")
    private String googleClientId;

    @Value("${google-client.secret}")
    private String googleClientSecret;
    @Value("${google-client.callback-uri}")
    private String googleCallBackUri;
    @Value("${bucket.name}")
    private String bucket;

    private SendStrategy sendStrategy;

    @Override
    public Email sendEmail(Email email, String login, MultipartFile file)
            throws MessagingException, IOException {
        MailBox mailBox = findMailBox(email.getSenderEmail(), login);
        specifyStrategyByMailBox(mailBox);
        var sentEmail = sendStrategy.sendWithStrategyEmail(email, mailBox, file);
        if(file != null && !file.isEmpty()){
            Tika tika = new Tika();
            String detectedType = tika.detect(file.getBytes()).split("/")[1];
            var fileKey = login + "/" + UUID.randomUUID().toString() + "." + detectedType;
            try {
                storageService.uploadFile(
                        file.getBytes(),
                        fileKey,
                        bucket);
                System.out.println("File uploaded successfully");
            } catch (IOException e) {
                var fileName = file.getOriginalFilename();
                throw new SendException("Failed to upload file %s".formatted(fileName));
            }
            sentEmail.setImageKey(fileKey);
        }
        sentEmail.setUser(mailBox.getUser());
        emailRepository.save(sentEmail);
        return sentEmail;
    }

    @Override
    public MailBox addEmailConfiguration(MailBox mailBox) throws MessagingException, IOException {
        checkIfMailBoxExists(mailBox.getEmailAddress(), mailBox.getUser().getLogin());
        String domainPart = extractEmailDomain(mailBox.getEmailAddress());
        EmailConfiguration emailConfig = findEmailConfiguration(domainPart);

        mailBox.setEmailConfiguration(emailConfig);
        if (emailConfig == EmailConfiguration.GMAIL) {
            getAccessToken(mailBox);
        }

        validateCredentials(mailBox);
        mailBoxRepository.save(mailBox);
        return mailBox;
    }


    @Transactional
    @Override
    public void refreshGmailTokens() {
        List<MailBox> gmailMailboxes =
                mailBoxRepository.findByEmailConfiguration(EmailConfiguration.GMAIL);

        for (MailBox mailbox : gmailMailboxes) {
            try {
                TokenResponse response = refreshAccessToken(mailbox.getRefreshToken());
                mailbox.setAccessSmtp(response.getAccessToken());
                mailBoxRepository.save(mailbox);
            } catch (IOException e) {
                System.err.printf("Failed to refresh token for email: %s%n",
                        mailbox.getEmailAddress());
                e.printStackTrace();
            }
        }
    }

    private MailBox findMailBox(String emailAddress, String login) {
        return mailBoxRepository.findByEmailAddressAndUserLogin(emailAddress, login)
                .orElseThrow(() -> new MailboxNotFoundException(
                        "Mail box not found for email: " + emailAddress));
    }

    private void validateCredentials(MailBox mailBox) throws MessagingException {
        specifyStrategyByMailBox(mailBox);
        sendStrategy.checkIsPasswordCorrect(mailBox);
    }

    private void checkIfMailBoxExists(String emailAddress, String login) {
        mailBoxRepository.findByEmailAddressAndUserLogin(emailAddress, login)
                .ifPresent(mailbox -> {
                    throw new MailBoxAlreadyExistsException(
                            "Email box already exists: " + emailAddress);
                });
    }

    private EmailConfiguration findEmailConfiguration(String domainPart) {
        return Arrays.stream(EmailConfiguration.values())
                .filter(config -> config.getDomainName().equalsIgnoreCase(domainPart))
                .findFirst()
                .orElseThrow(
                        () -> new InvalidEmailDomainException("Unsupported domain: " + domainPart));
    }
    @Override
    public String extractEmailDomain(String emailAddress) {
        Pattern pattern = Pattern.compile("(?<=@)[^.]+(?=\\.)");
        Matcher matcher = pattern.matcher(emailAddress);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalArgumentException("Invalid email address format: " + emailAddress);
    }

    private void specifyStrategyByMailBox(MailBox mailBox) {
        String strategyBeanName = mailBox.getEmailConfiguration().getDomainName();

        sendStrategy = listOfStrategies.stream()
                .filter(strategy -> strategy.getClass().isAnnotationPresent(Service.class) &&
                        strategy.getClass().getAnnotation(Service.class).value()
                                .equalsIgnoreCase(strategyBeanName))
                .findFirst()
                .orElseThrow(() -> new StrategyNotFoundException(
                        "No SendStrategy found for: " + strategyBeanName));
    }

    private TokenResponse refreshAccessToken(String refreshToken) throws IOException {
        return new GoogleRefreshTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, refreshToken,
                googleClientId, googleClientSecret)
                .execute();
    }

    private void getAccessToken(MailBox mailBox) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();

        String tokenServerUrl = "https://accounts.google.com/o/oauth2/token";

        AuthorizationCodeTokenRequest
                request = new AuthorizationCodeTokenRequest(httpTransport, jsonFactory,
                new GenericUrl(tokenServerUrl), mailBox.getAccessSmtp());

        request.setClientAuthentication(
                new ClientParametersAuthentication(googleClientId, googleClientSecret));
        request.setRedirectUri(googleCallBackUri);
        TokenResponse response = request.execute();

        mailBox.setAccessSmtp(response.getAccessToken());
        mailBox.setRefreshToken(response.getRefreshToken());
        System.out.println("Access Token: " + response.getAccessToken());
        System.out.println("Refresh Token: " + response.getRefreshToken());
        System.out.println("Expires In: " + response.getExpiresInSeconds() + " seconds");
    }
}
