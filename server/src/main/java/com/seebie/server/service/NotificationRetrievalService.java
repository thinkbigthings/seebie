package com.seebie.server.service;

import com.seebie.server.entity.Notification;
import com.seebie.server.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@EnableScheduling
@Service
public class NotificationRetrievalService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationRetrievalService.class);

    private final NotificationRepository notificationRepo;

    public NotificationRetrievalService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    /**
     * Separate out the finding of people to send notifications from the email sending logic.
     * Transactional must be called from a separate class from the Scheduled method anyway.
     *
     * This sets the last sent time of the Notification record,
     * that way another transaction that tries to read that record will ignore it.
     *
     * @return
     */
    @Transactional
    public List<NotificationEmailService.SendNotification> getUsersToNotify() {

        LOG.info("Retrieving Notifications for emails");

        var now = Instant.now();
        var yesterday = now.minus(Duration.of(24, ChronoUnit.SECONDS));

        // Don't set up separate nodes just for running scheduled tasks.
        // To run from the webserver in a multi-node system, get an exclusive read-write lock on the notification record
        // Test with multiple servers running locally

        // TODO write a query to get notification objects whose sleep is in the notifiable range
        //  SleepSession has a user, can join on the user id

        // Don't want to send notifications every hour!
        // If last notification was >= 24 hours ago AND the most recent session occurred >= 30 hours ago:
        // send a notification and update latest time, user gets an email once per day until logging something.

        // TODO capture exceptions from inside a stream and continue the stream - could happen trying to obtain a lock

        return notificationRepo.findNotificationsBefore(yesterday).stream()
                .map(notification -> notification.withLastSent(now))
                .map(Notification::getUser)
                .map(user -> new NotificationEmailService.SendNotification(user.getEmail(), user.getUsername()))
                .toList();
    }
}
