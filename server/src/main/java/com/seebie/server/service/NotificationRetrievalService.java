package com.seebie.server.service;

import com.seebie.server.entity.Notification;
import com.seebie.server.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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
    public List<NotificationRequired> getUsersToNotify(Instant ifNotNotifiedSince, Instant ifNotLoggedSince, Instant newLastScan) {

        LOG.info("Retrieving Notifications for emails");

        return notificationRepo.findNotificationsBy(ifNotNotifiedSince, ifNotLoggedSince).stream()
                .map(notification -> notification.withLastSent(newLastScan))
                .map(Notification::getUser)
                .map(user -> new NotificationRequired(user.getEmail(), user.getUsername()))
                .toList();
    }
}
