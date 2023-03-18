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

@EnableScheduling
@Service
public class NotificationMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationMessageService.class);

    private final JavaMailSender emailSender;
    private final NotificationRetrievalService notificationRetrievalService;
    private boolean scanEnabled;
    private NotificationOutput notificationOutput;

    private final SimpleMailMessage emailTemplate = new SimpleMailMessage();

    public NotificationMessageService(NotificationRetrievalService notificationRetrievalService, JavaMailSender emailSender, Environment env) {

        this.notificationRetrievalService = notificationRetrievalService;
        this.emailSender = emailSender;

        scanEnabled = env.getRequiredProperty("app.notification.scan.enabled", Boolean.class);
        notificationOutput = env.getRequiredProperty("app.notification.output", NotificationOutput.class);

        emailTemplate.setFrom(env.getProperty("spring.mail.username"));
        emailTemplate.setSubject("Missing Sleep Log");
        emailTemplate.setText("Hi %s you missed recording your last sleep session...");

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
     * Use fixedDelay so that we execute the annotated method with a fixed period
     * between the end of the last invocation and the start of the next.
     * That way we avoid overlapping executions.
     *
     * Keep the scan turned off for integration tests, so it doesn't interfere with the notification integration tests.
     */
    @Scheduled(fixedDelayString="${app.notification.scan.schedule.fixedDelay}", timeUnit = TimeUnit.MINUTES)
    public void scanForNotifications() {

        if( !scanEnabled) {
            return;
        }

        LOG.info("Email notifications starting...");

        var now = Instant.now();
        var ifNotNotifiedSince = now.minus(Duration.of(24, ChronoUnit.SECONDS));
        var ifNotLoggedSince = now.minus(Duration.of(30, ChronoUnit.SECONDS));

        var listToSend = notificationRetrievalService.getUsersToNotify(ifNotNotifiedSince, ifNotLoggedSince, now);

        LOG.info("Email notifications found " + listToSend.size() + " users to notify");

        listToSend.forEach(this::sendEmail);

        LOG.info("Email notifications complete.");
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
        message.setText(String.format(emailTemplate.getText(), send.username()));

        return message;
    }

}
