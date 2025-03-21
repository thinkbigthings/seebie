package com.seebie.server.service;

import com.seebie.server.dto.RegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSender;

import java.util.Arrays;
import java.util.UUID;

import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static com.seebie.server.test.data.TestData.newAppProperties;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationEmailServiceTest {

    private MailSender mailSender = Mockito.mock(MailSender.class);
    private NotificationRetrievalService retrievalService = Mockito.mock(NotificationRetrievalService.class);

    private NotificationMessageService service;

    @BeforeEach
    public void setup() {

        service = new NotificationMessageService(retrievalService, mailSender, newAppProperties(30));
    }

    @Test
    public void testMailTemplate() {

        RegistrationRequest user = createRandomUserRegistration();
        var publicId = UUID.randomUUID();
        var message = service.createMessage(new NotificationRequired(user.email(), publicId));

        assertTrue(Arrays.stream(message.getTo()).anyMatch(email -> email.equals(user.email())));
    }

}
