package com.seebie.server.service;

import com.seebie.server.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
public class NotificationMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationMessageService.class);

    private final MailSender emailSender;
    private final NotificationRetrievalService notificationRetrievalService;

    private AppProperties.Notification notification;

    private final SimpleMailMessage emailTemplate;

    // set the value to the string "undefined" if the property is not set anywhere
    @Value("${spring.mail.username:undefined}")
    private String fromEmail;

    public NotificationMessageService(NotificationRetrievalService notificationRetrievalService, MailSender emailSender, AppProperties appProperties) {

        this.notificationRetrievalService = notificationRetrievalService;
        this.emailSender = emailSender;
        this.notification = appProperties.notification();

        emailTemplate = new SimpleMailMessage();
        emailTemplate.setFrom(fromEmail);
        emailTemplate.setSubject("Missing Sleep Log");

        LOG.info("Notification configuration is " + notification.toString());
    }


    /**
     * As opposed to setting up a separate node just for running scheduled tasks,
     * We can safely run from the webserver in a multi-node system
     * by obtaining an exclusive read-write lock on the notification records.
     * See the other Notification related classes for more technical details.
     *
     * For example:
     * If last notification was >= 24 hours ago AND the last sleep logged was >= 30 hours ago:
     * send a notification and update the latest time, user gets an email once per day until logging something.
     *
     * Use fixedDelayMinutes so that we execute the annotated method with a fixed period
     * between the end of the last invocation and the start of the next.
     * That way we avoid overlapping executions.
     *
     * Keep the scan turned off for integration tests, so it doesn't interfere with the notification integration tests.
     * Don't swap out the entire message service implementation for testing, just disable the scan,
     * so that we can use bootTestRun to manually investigate the email sending logic if necessary.
     */
    @Scheduled(fixedDelayString="${app.notification.scanFrequencyMinutes}", timeUnit = TimeUnit.MINUTES)
    public void runOnSchedule() {

        if( ! notification.enabled()) {
            LOG.info("Email notifications schedule was triggered but scan was disabled.");
            return;
        }

        LOG.info("Email notifications scan is starting...");

        var listToSend = findUsersToNotify(Instant.now());

        LOG.info("Email notifications found " + listToSend.size() + " users to notify");

        listToSend.forEach(this::sendEmail);

        LOG.info("Email notifications complete.");
    }

    public List<NotificationRequired> findUsersToNotify(Instant now) {
        var ifNotNotifiedSince = now.minus(notification.triggerAfter().lastNotified());
        var ifNotLoggedSince = now.minus(notification.triggerAfter().sleepLog());
        return notificationRetrievalService.getUsersToNotify(ifNotNotifiedSince, ifNotLoggedSince, now);
    }

    private void sendEmail(NotificationRequired send) {

        try {
            LOG.info("Email notification going out to " + send.email());
            emailSender.send(createMessage(send));
        }
        catch(MailException me) {
            LOG.info("Email notification failed to send for " + send.email());
            me.printStackTrace();
        }

    }

    public SimpleMailMessage createMessage(NotificationRequired send) {

        String text = STR."""
        Hi \{send.username()},
        You missed recording your last sleep session. If you record it right away you won't lose your momentum!
        FYI you can control these notifications in your user settings.
        """;

        var message = new SimpleMailMessage(emailTemplate);

        message.setTo(send.email());
        message.setText(text);

        return message;
    }

}
