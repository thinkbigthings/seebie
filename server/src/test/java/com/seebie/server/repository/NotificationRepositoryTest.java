package com.seebie.server.repository;

import com.seebie.server.test.IntegrationTest;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.Notification;
import com.seebie.server.entity.User;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class NotificationRepositoryTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private NotificationRepository notificationRepository;

    private static final ZonedDateTime today = ZonedDateTime.now();
    private static final ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
    private static final SleepData lastSleep = new SleepData(yesterday, today);

    private static List<Arguments> provideThresholdOffsets() {

        return List.of(

                Arguments.of(-60,  60, false, lastSleep),  //     notified recent, not logged recent: not notified
                Arguments.of(-60, -60, false, lastSleep),  //     notified recent,     logged recent: not notified
                Arguments.of( 60,  60, true,  lastSleep),  // not notified recent, not logged recent:     notified
                Arguments.of( 60, -60, false, lastSleep),  // not notified recent, not logged recent: not notified
                Arguments.of( 60,  60, false, null)        //     notified recent,     logged recent: not notified (no sleep ever logged)
        );
    }

    @ParameterizedTest
    @MethodSource("provideThresholdOffsets")
    @DisplayName("Retrieve notification for missed sleep")
    @Transactional
    public void testHasNotification(int notificationOffset, int sleepLogOffset, boolean expectNotification, SleepData lastSleep) {

        // create new user and enable notifications
        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        String username = testUserRegistration.username();
        userService.updateUser(username, userService.getUser(username).personalInfo().withNotificationEnabled(true));

        // save sleep session if user logged it
        Optional.ofNullable(lastSleep).ifPresent(s -> sleepService.saveNew(username, s));

        // Imagine we've moved forward in time,
        // to the point where what's in the database is older than the threshold for both triggers,
        // in which case notification records should be retrieved
        var notify = notificationRepository.findBy(username).get();
        var lastNotification = notify.getLastSent();
        var lastSleepLog = today;
        var notificationTrigger = lastNotification.plusSeconds(notificationOffset);
        var sleepTrigger = lastSleepLog.plusSeconds(sleepLogOffset).toInstant();

        // test for user in results, so test is scoped and can be run in parallel
        // even though the query is designed to operate across all data in the database
        boolean userHasNotification = notificationRepository.findNotificationsBy(notificationTrigger, sleepTrigger).stream()
                .map(Notification::getUser)
                .map(User::getUsername)
                .anyMatch(name -> name.equals(username));

        assertEquals(expectNotification, userHasNotification);
    }

}
