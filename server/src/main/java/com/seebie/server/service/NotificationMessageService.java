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
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
public class NotificationMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationMessageService.class);

    private final JavaMailSender emailSender;
    private final NotificationRetrievalService notificationRetrievalService;
    private boolean scanEnabled;
    private NotificationOutput notificationOutput;
    private Duration triggerAfterLastNotified;
    private Duration triggerAfterSleepLog;

    private final SimpleMailMessage emailTemplate = new SimpleMailMessage();

    /**
     * This takes an Environment object instead of a Java Record for the properties because it wouldn't work with the
     * Spring properties (here, spring.mail.username could not be injected as a custom nested record into AppProperties).
     * So since an Environment has to be used here anyway, we might as well use it for all the properties.
     *
     * @param notificationRetrievalService
     * @param emailSender
     * @param env
     */
    public NotificationMessageService(NotificationRetrievalService notificationRetrievalService, JavaMailSender emailSender, Environment env) {

        this.notificationRetrievalService = notificationRetrievalService;
        this.emailSender = emailSender;

        scanEnabled = env.getRequiredProperty("app.notification.scan.enabled", Boolean.class);
        notificationOutput = env.getRequiredProperty("app.notification.output", NotificationOutput.class);

        triggerAfterLastNotified = env.getRequiredProperty("app.notification.triggerAfter.lastNotified", Duration.class);
        triggerAfterSleepLog = env.getRequiredProperty("app.notification.triggerAfter.sleepLog", Duration.class);

        emailTemplate.setFrom(env.getProperty("spring.mail.username"));
        emailTemplate.setSubject("Missing Sleep Log");
        emailTemplate.setText("");

        LOG.info("Instantiated Notification Service.");
        LOG.info("Scan schedule enabled is " + scanEnabled);
        LOG.info("Notification message target is " + notificationOutput);
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
     */
    @Scheduled(fixedDelayString="${app.notification.scan.schedule.fixedDelayMinutes}", timeUnit = TimeUnit.MINUTES)
    public void runOnSchedule() {

        if( ! scanEnabled) {
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
        var ifNotNotifiedSince = now.minus(triggerAfterLastNotified);
        var ifNotLoggedSince = now.minus(triggerAfterSleepLog);
        return notificationRetrievalService.getUsersToNotify(ifNotNotifiedSince, ifNotLoggedSince, now);
    }

    private void sendEmail(NotificationRequired send) {

        try {

            LOG.info("Email notification going out to " + send.email());

            var message = createMessage(send);

            switch(notificationOutput) {
               case EMAIL -> emailSender.send(message);
               case LOG -> LOG.info(message.toString());
            }
        }

        catch(MailException me) {

            LOG.info("Email notification failed to send for " + send.email());

            me.printStackTrace();
        }

    }

    public SimpleMailMessage createMessage(NotificationRequired send) {

        var message = new SimpleMailMessage(emailTemplate);

        message.setTo(send.email());
        String text = STR."""
        Hi \{send.username()},
        You missed recording your last sleep session. If you record it right away you won't lose your momentum!
        FYI you can control these notifications in your user settings.
        """;
        message.setText(text);

        return message;
    }

}
