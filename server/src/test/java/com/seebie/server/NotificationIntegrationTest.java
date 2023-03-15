package com.seebie.server;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.repository.NotificationRepository;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class NotificationIntegrationTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private NotificationRepository notificationRepository;


    // TODO enumerate test cases - parameterize?
    // log     recent, not notified recent: not notified
    // log not recent, not notified recent:     notified
    // log     recent,     notified recent: not notified
    // log not recent,     notified recent: not notified
    // no sleep ever logged

    @Test
    @DisplayName("Retrieve notification for missed sleep")
    @Transactional
    public void testHasNotification() {

        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        String username = testUserRegistration.username();


        // make this all in the future so the test is not affected by other users and their sleep and notifications
        // once we have a flag to determine if a user's notifications can be set, then that won't matter
        // TODO use regular sleep data once we have a flag
        SleepData lastSleep = new SleepData(ZonedDateTime.now().plusDays(1), ZonedDateTime.now().plusDays(2));

        sleepService.saveNew(username, lastSleep);

        var notify = notificationRepository.findBy(username).get();

        var lastNotification = notify.getLastSent();
        var lastSleepLog = lastSleep.stopTime();

        // Imagine we've gone forward in time
        // to the point where what's in the database is older than the threshold for each trigger,
        // in which case notification records should be retrieved
        var notificationTrigger = lastNotification.plusSeconds(60);
        var sleepTrigger = lastSleepLog.plusSeconds(60);

        var foundNotifications = notificationRepository.findNotificationsBy(notificationTrigger, sleepTrigger);

        assertEquals(1, foundNotifications.size());
    }


}
