package com.seebie.server.service;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.service.NotificationMessageService;
import com.seebie.server.service.NotificationRequired;
import com.seebie.server.service.NotificationRetrievalService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class NotificationEmailServiceTest {

    private JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
    private NotificationRetrievalService retrievalService = Mockito.mock(NotificationRetrievalService.class);
    private Environment env = Mockito.mock(Environment.class);

    private final String sender = "user@email.com";

    private NotificationMessageService service;

    @BeforeEach
    public void setup() {

        when(env.getProperty(eq("spring.mail.username"))).thenReturn(sender);
        when(env.getRequiredProperty(eq("app.notification.scan.enabled"), eq(Boolean.class))).thenReturn(false);

        service = new NotificationMessageService(retrievalService, mailSender, env);
    }

    @Test
    public void testMailTemplate() {

        RegistrationRequest user = TestData.createRandomUserRegistration();

        var message = service.createMessage(new NotificationRequired(user.email(), user.username()));

        assertEquals(message.getFrom(), sender);
        assertTrue(message.getText().contains(user.username()));
        assertTrue(Arrays.stream(message.getTo()).anyMatch(email -> email.equals(user.email())));
    }

}
