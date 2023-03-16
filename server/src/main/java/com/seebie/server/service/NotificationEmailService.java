package com.seebie.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

//@EnableScheduling
@Service
public class NotificationEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationEmailService.class);

    private final JavaMailSender emailSender;
    private final NotificationRetrievalService notificationRetrievalService;

    private final SimpleMailMessage emailTemplate = new SimpleMailMessage();

    public NotificationEmailService(NotificationRetrievalService notificationRetrievalService, JavaMailSender emailSender, Environment env) {

        this.notificationRetrievalService = notificationRetrievalService;
        this.emailSender = emailSender;

        emailTemplate.setFrom(env.getProperty("spring.mail.username"));
        emailTemplate.setSubject("Missing Sleep Log");
        emailTemplate.setText("Hi %s you missed recording your last sleep session...");
    }

    // TODO does this schedule run on period or after completion of last run? What if it runs on period and there's overlap?
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void scanForNotifications() {

        LOG.info("Email notifications starting...");

        var now = Instant.now();
        var ifNotNotifiedSince = now.minus(Duration.of(24, ChronoUnit.SECONDS));
        var ifNotLoggedSince = now.minus(Duration.of(30, ChronoUnit.SECONDS));

        // If last notification was >= 24 hours ago AND the most recent session occurred >= 30 hours ago:
        // send a notification and update latest time, user gets an email once per day until logging something.

        var listToSend = notificationRetrievalService.getUsersToNotify(ifNotNotifiedSince, ifNotLoggedSince);

        LOG.info("Email notifications found " + listToSend.size() + " users to notify");

        listToSend.forEach(this::sendEmail);

        LOG.info("Email notifications complete.");
    }

    private void sendEmail(NotificationRequired send) {

        try {

            LOG.info("Email going out to " + send.email());

            // TODO sending email is a blocking operation, could be spun out into own virtual threads
            // Java 19 has StructuredConcurrency
            // emailSender.send(createMessage(send));
        }
        catch(MailException me) {
            me.printStackTrace();
        }

    }

    public SimpleMailMessage createMessage(NotificationRequired send) {

        var message = new SimpleMailMessage(emailTemplate);

        message.setTo(send.email());
        message.setText(String.format(emailTemplate.getText(), send.username()));

        return message;
    }

}
