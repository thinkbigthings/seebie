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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
public class NotificationMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationMessageService.class);

    private final MailSender emailSender;
    private final NotificationRetrievalService notificationRetrievalService;

    private AppProperties.Notification notificationConfig;

    private final SimpleMailMessage emailTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public NotificationMessageService(NotificationRetrievalService notificationRetrievalService, MailSender emailSender, AppProperties appProperties) {

        this.notificationRetrievalService = notificationRetrievalService;
        this.emailSender = emailSender;
        this.notificationConfig = appProperties.notification();

        emailTemplate = new SimpleMailMessage();
        emailTemplate.setFrom(fromEmail);
        emailTemplate.setSubject("Missing Sleep Log");
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
     */
    @Scheduled(fixedRateString="${app.notification.scanFrequencyMinutes}", timeUnit = TimeUnit.MINUTES)
    public void runOnSchedule() {

        LOG.debug("Email notifications scan is starting...");

        var listToSend = findUsersToNotify(Instant.now());

        LOG.debug(STR."Email notifications found \{listToSend.size()} users to notify");

        listToSend.stream()
                .map(this::createMessage)
                .forEach(this::sendEmail);

        LOG.debug("Email notifications complete.");
    }

    public List<NotificationRequired> findUsersToNotify(Instant now) {
        var ifNotNotifiedSince = now.minus(notificationConfig.triggerAfter().lastNotified());
        var ifNotLoggedSince = now.minus(notificationConfig.triggerAfter().sleepLog());
        return notificationRetrievalService.getUsersToNotify(ifNotNotifiedSince, ifNotLoggedSince, now);
    }

    public void sendEmail(SimpleMailMessage message) {

        try {
            LOG.debug(STR."Email notification is going out to \{Arrays.asList(message.getTo())}");
            emailSender.send(message);
        }
        catch(MailException me) {
            LOG.info(STR."Email notification failed to send for \{Arrays.asList(message.getTo())}");
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
