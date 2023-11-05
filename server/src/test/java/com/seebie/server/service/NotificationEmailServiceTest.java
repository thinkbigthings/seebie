package com.seebie.server.service;

import com.seebie.server.dto.RegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Arrays;

import static com.seebie.server.AppProperties.newAppProperties;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class NotificationEmailServiceTest {

    private JavaMailSenderImpl mailSender = Mockito.mock(JavaMailSenderImpl.class);
    private NotificationRetrievalService retrievalService = Mockito.mock(NotificationRetrievalService.class);

    private final String sender = "user@email.com";

    private NotificationMessageService service;

    @BeforeEach
    public void setup() {

        when(mailSender.getUsername()).thenReturn(sender);

        service = new NotificationMessageService(retrievalService, mailSender, newAppProperties(30));
    }

    @Test
    public void testMailTemplate() {

        RegistrationRequest user = createRandomUserRegistration();

        var message = service.createMessage(new NotificationRequired(user.email(), user.username()));

        assertEquals(message.getFrom(), sender);
        assertTrue(message.getText().contains(user.username()));
        assertTrue(Arrays.stream(message.getTo()).anyMatch(email -> email.equals(user.email())));
    }

}
