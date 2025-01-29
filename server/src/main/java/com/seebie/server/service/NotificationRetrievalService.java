package com.seebie.server.service;

import com.seebie.server.entity.Notification;
import com.seebie.server.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Service
public class NotificationRetrievalService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationRetrievalService.class);

    private final NotificationRepository notificationRepo;

    public NotificationRetrievalService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    /**
     * Separate out the finding of people to send notifications, from the email sending logic.
     * A @Transactional method must be called from a separate class from the Scheduled method anyway.
     *
     * This sets the last sent time of the Notification record,
     * that way another scan transaction (maybe by another node) that tries to read that record will ignore it.
     *
     * @return
     */
    @Transactional
    public List<NotificationRequired> getUsersToNotify(Instant lastNotifiedBefore, Instant lastLoggedBefore, Instant scanDate) {

        LOG.debug("Retrieving Notification records for users.");

        LOG.debug("Notifications will be sent to users who have not been notified since " + toLocale(lastNotifiedBefore)
                + "and have not logged their sleep since " + toLocale(lastLoggedBefore) + "."
                + "Last scan date for notified users is set to " + toLocale(scanDate) + ".");

        return notificationRepo.findNotificationsBy(lastNotifiedBefore, lastLoggedBefore).stream()
                .peek(notification -> LOG.info(createLogMessage(notification, scanDate)))
                .map(notification -> notification.withLastSent(scanDate))
                .map(Notification::getUser)
                .map(user -> new NotificationRequired(user.getEmail(), user.getPublicId()))
                .toList();
    }

    private String createLogMessage(Notification notification, Instant scanDate) {
        var user = notification.getUser().getPublicId();
        var lastSentDate = toLocale(notification.getLastSent());
        return "Updating notification record for " + user + " from " + lastSentDate + " to " + scanDate;
    }

    public static String toLocale(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).format(ISO_OFFSET_DATE_TIME);
    }

}
