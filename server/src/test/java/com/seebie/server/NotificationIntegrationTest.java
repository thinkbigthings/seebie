package com.seebie.server;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.Notification;
import com.seebie.server.entity.User;
import com.seebie.server.repository.NotificationRepository;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class NotificationIntegrationTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private NotificationRepository notificationRepository;

    private static List<Arguments> provideThresholdOffsets() {

        return List.of(

                Arguments.of(-60,  60, false), //     notified recent, not logged recent: not notified
                Arguments.of(-60, -60, false), //     notified recent,     logged recent: not notified
                Arguments.of( 60,  60, true ), // not notified recent, not logged recent:     notified
                Arguments.of( 60, -60, false)  // not notified recent, not logged recent: not notified
        );
    }

    @ParameterizedTest
    @MethodSource("provideThresholdOffsets")
    @DisplayName("Retrieve notification for missed sleep")
    @Transactional
    public void testHasNotification(int notificationOffset, int sleepLogOffset, boolean expectNotification) {

        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        String username = testUserRegistration.username();


        // make this all in the future so the test is not affected by other users and their sleep and notifications
        // once we have a flag to determine if a user's notifications can be set, then that won't matter
        // TODO use regular sleep data once we have a flag

        // keep the sleep data the same, just change the offsets we'll use to detect if it's past due
        // For the notification case:
        // Imagine we've moved forward in time
        // to the point where what's in the database is older than the threshold for each trigger,
        // in which case notification records should be retrieved

        SleepData lastSleep = new SleepData(ZonedDateTime.now().plusDays(1), ZonedDateTime.now().plusDays(2));

        sleepService.saveNew(username, lastSleep);

        var notify = notificationRepository.findBy(username).get();

        var lastNotification = notify.getLastSent();
        var lastSleepLog = lastSleep.stopTime();

        var notificationTrigger = lastNotification.plusSeconds(notificationOffset);
        var sleepTrigger = lastSleepLog.plusSeconds(sleepLogOffset).toInstant();

        var foundNotifications = notificationRepository.findNotificationsBy(notificationTrigger, sleepTrigger);

        // test for user in results, so test is scoped and can be run in parallel
        // even though the query is designed to operate across all data in the database

        boolean userHasNotification = foundNotifications.stream()
                .map(Notification::getUser)
                .map(User::getUsername)
                .anyMatch(name -> name.equals(username));

        assertEquals(expectNotification, userHasNotification);


        // TODO refactor SendNotification record

        // TODO test for no sleep ever logged

    }


}
