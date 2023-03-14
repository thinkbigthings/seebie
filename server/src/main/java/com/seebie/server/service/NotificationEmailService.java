package com.seebie.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
public class NotificationEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationEmailService.class);

    private final JavaMailSender emailSender;

    private final SimpleMailMessage emailTemplate = new SimpleMailMessage();
    private final NotificationRetrievalService notificationRetrievalService;
    record SendNotification(String email, String username) {  }

    public NotificationEmailService(NotificationRetrievalService notificationRetrievalService, JavaMailSender emailSender) {

        this.notificationRetrievalService = notificationRetrievalService;
        this.emailSender = emailSender;

        // TODO email message should setFrom() using known spring properties

        emailTemplate.setFrom("thinkbigthings@gmail.com");
        emailTemplate.setSubject("Missing Sleep Log");
        emailTemplate.setText("You missed recording your last sleep session...");
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void scanForNotifications() {

        LOG.info("Email notifications starting...");

        var listToSend = notificationRetrievalService.getUsersToNotify();

        LOG.info("Email notifications found " + listToSend.size() + " users to notify");

        listToSend.forEach(this::sendEmail);

        LOG.info("Email notifications complete.");

    }

    private void sendEmail(SendNotification send) {

        try {

            LOG.info("Email going out to " + send.email());

            // TODO sending could be spun out into own threads / CompletableFutures
            // emailSender.send(createMessage(send));
        }
        catch(MailException me) {
            me.printStackTrace();
        }

    }

    public SimpleMailMessage createMessage(SendNotification send) {

        var message = new SimpleMailMessage(emailTemplate);

        // TODO apply text with string template, try String.format(template.getText(), templateArgs);
        // text should include a link to the app
        // mention how to turn notifications off and include a link to user settings.

        return message;
    }

}
