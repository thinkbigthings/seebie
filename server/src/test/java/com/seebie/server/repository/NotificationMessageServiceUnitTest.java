package com.seebie.server.repository;


import com.seebie.server.AppProperties;
import com.seebie.server.service.NotificationMessageService;

import com.seebie.server.service.NotificationRetrievalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class NotificationMessageServiceUnitTest {

    private NotificationRetrievalService notificationRetrievalService = mock(NotificationRetrievalService.class);
    private MailSender emailSender = mock(MailSender.class);
    private AppProperties appProperties = mock(AppProperties.class);

    private NotificationMessageService notificationService = new NotificationMessageService(notificationRetrievalService, emailSender, appProperties);

    SimpleMailMessage message;

    @BeforeEach
    public void setup() {
        message = new SimpleMailMessage();
        message.setTo("recipient");
    }

    @Test
    public void testEmailExceptionsAreCaught() {

        doThrow(new MailSendException("Test Exception"))
                .when(emailSender)
                .send(any(SimpleMailMessage.class));

        // should pass - nothing happens because the exception was caught
        notificationService.sendEmail(message);
    }

    @Test
    public void testEmailSender() {

        notificationService.sendEmail(message);

        verify(emailSender).send(message);
    }

}
