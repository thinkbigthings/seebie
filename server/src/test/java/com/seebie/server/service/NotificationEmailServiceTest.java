package com.seebie.server.service;

import com.seebie.server.dto.RegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Arrays;
import java.util.UUID;

import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static com.seebie.server.test.data.TestData.newAppProperties;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class NotificationEmailServiceTest {

    private MailSender mailSender = Mockito.mock(MailSender.class);
    private NotificationRetrievalService retrievalService = Mockito.mock(NotificationRetrievalService.class);

    private NotificationMessageService notificationService;

    @BeforeEach
    public void setup() {

        var properties = newAppProperties(30);
        notificationService = new NotificationMessageService(retrievalService, mailSender, properties);

    }

    @Test
    public void testMailTemplate() {

        RegistrationRequest user = createRandomUserRegistration();
        var publicId = UUID.randomUUID();
        var message = notificationService.createMessage(new NotificationRequired(user.email(), publicId));

        assertNotNull(message.getTo());

        var targetEmails = Arrays.asList(message.getTo());
        assertEquals(1, targetEmails.size());
        assertEquals(user.email(), targetEmails.getFirst());
    }

    @Test
    public void testEmailExceptionsAreCaught() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient");

        doThrow(new MailSendException("Test Exception"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        // should pass - nothing happens because the exception was caught
        notificationService.sendEmail(message);
    }

    @Test
    public void testEmailSender() {

        // This could be tested with the MemoryAppender approach and MailSenderToLogs test bean,
        // but the existing unit test is more efficient and tests essentially the same thing.
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient");

        notificationService.sendEmail(message);

        verify(mailSender).send(message);
    }

}
