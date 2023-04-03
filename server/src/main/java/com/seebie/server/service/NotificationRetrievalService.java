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
import java.util.Objects;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeConverter.format;

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
     * that way another scan transaction (maybe by another node) that tries to read that record will ignore it.
     *
     * @return
     */
    @Transactional
    public List<NotificationRequired> getUsersToNotify(Instant lastNotifiedBefore, Instant lastLoggedBefore, Instant scanDate) {

        LOG.info("Retrieving Notification records for users.");
        LOG.info("Notifications will be sent to users " +
                "who have not been notified since " + toLocale(lastNotifiedBefore) + " " +
                "and have not logged sleep since " + toLocale(lastLoggedBefore) + ". " +
                "Last scan date for notified users will be set to " + scanDate + "."
            );

        return notificationRepo.findNotificationsBy(lastNotifiedBefore, lastLoggedBefore).stream()
                .peek(notification -> LOG.info(createLogMessage(notification, scanDate)))
                .map(notification -> notification.withLastSent(scanDate))
                .map(Notification::getUser)
                .map(user -> new NotificationRequired(user.getEmail(), user.getUsername()))
                .toList();
    }

    private String createLogMessage(Notification notification, Instant scanDate) {
        return "Updating notification record for " + notification.getUser().getUsername()
                + " from " + toLocale(notification.getLastSent())
                + " to " + scanDate;
    }

    public static String toLocale(Instant instant) {
        return Objects.isNull(instant) ? "null" : format(instant.atZone(ZoneId.systemDefault()));
    }

}
