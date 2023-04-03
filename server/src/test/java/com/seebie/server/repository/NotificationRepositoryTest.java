package com.seebie.server.repository;

import com.seebie.server.service.NotificationRequired;
import com.seebie.server.service.NotificationRetrievalService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeConverter.format;
import static com.seebie.server.service.NotificationRetrievalService.toLocale;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class NotificationRepositoryTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private NotificationRetrievalService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private static final ZonedDateTime now = ZonedDateTime.now();
    private static final SleepData sleepToday = new SleepData(ZonedDateTime.now().minusHours(8), now);

    // app.notification.triggerAfter.sleepLog=30h
    // pp.notification.triggerAfter.lastNotified=24h
    private Duration triggerAfterSleepLogAged = Duration.ofHours(30);
    private Duration triggerAfterLastNotifiedAged = Duration.ofHours(24);

    private static List<Arguments> provideThresholdOffsets() {

        return List.of(

                // notifications enabled
                Arguments.of(true,  false, false, sleepToday, true),  //     notified recent, not logged recent: not notified
                Arguments.of(true,  true,  false, sleepToday, true),  //     notified recent,     logged recent: not notified
                Arguments.of(false, false, true,  sleepToday, true),  // not notified recent, not logged recent:     notified
                Arguments.of(false, true,  false, sleepToday, true),  // not notified recent,     logged recent: not notified
                Arguments.of(true,  true,  false, null,       true),  //     notified recent,                  : not notified (no sleep ever logged)

                // notifications disabled
                Arguments.of(true,  false, false, sleepToday, false),  //     notified recent, not logged recent: not notified
                Arguments.of(true,  true,  false, sleepToday, false),  //     notified recent,     logged recent: not notified
                Arguments.of(false, false, false, sleepToday, false),  // not notified recent, not logged recent: not notified (would have been if enabled)
                Arguments.of(false, true,  false, sleepToday, false),  // not notified recent,     logged recent: not notified
                Arguments.of(true,  true,  false, null,       false)   //     notified recent,                  : not notified (no sleep ever logged)
        );
    }

    @ParameterizedTest
    @MethodSource("provideThresholdOffsets")
    @DisplayName("Retrieve notification for missed sleep")
    @Transactional
    public void testTest(boolean lastLogWasRecent, boolean lastNotifiedWasRecent, boolean expectNotification, SleepData lastSleep, boolean notificationsEnabled) {

        Duration lastLoggedAgo = lastLogWasRecent ? triggerAfterSleepLogAged.minusHours(1) : triggerAfterSleepLogAged.plusHours(1);
        Duration lastNotifiedAgo = lastNotifiedWasRecent ? triggerAfterLastNotifiedAged.minusHours(1) : triggerAfterLastNotifiedAged.plusHours(1);

        // create new user and enable notifications
        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        String username = testUserRegistration.username();
        userService.updateUser(username, userService.getUser(username).personalInfo().withNotificationEnabled(notificationsEnabled));

        // save sleep session if user logged it
        var lastSleepLogged = Optional.ofNullable(lastSleep)
                .map(s -> TestData.decrement(s, lastLoggedAgo))
                .map(s -> sleepService.saveNew(username, s))
                .map(s -> s.sleepData().stopTime().toInstant())
                .orElse(null);

        // save last notification
        var notify = notificationRepository.findBy(username).get();
        notify.withLastSent(notify.getLastSent().minus(lastNotifiedAgo));

        String lastLoggedEst = toLocale(lastSleepLogged);
        String lastNotifiedEst = toLocale(notify.getLastSent());

        // TODO maybe property names should have "age" in them as a more descriptive name

        // TODO also do requests for after the notification is updated

        System.out.println("User's last notification was " + lastNotifiedEst);
        System.out.println("User's last sleep log was " + lastLoggedEst);

        var lastNotificationSentBefore = now.minus(triggerAfterLastNotifiedAged).toInstant();
        var lastSleepLoggedBefore = now.minus(triggerAfterSleepLogAged).toInstant();

        var notificationsToSend = notificationService.getUsersToNotify(lastNotificationSentBefore, lastSleepLoggedBefore, now.toInstant());

        // test for user in results, so test is scoped and can be run in parallel
        // even though the query is designed to operate across all data in the database
        boolean userHasNotification = notificationsToSend.stream()
                .map(NotificationRequired::username)
                .anyMatch(name -> name.equals(username));

        assertEquals(expectNotification, userHasNotification);


        // Test for after the notification is updated
        // if the user didn't need to be notified or was just notified, there should never be notifications sent
        notificationsToSend = notificationService.getUsersToNotify(lastNotificationSentBefore, lastSleepLoggedBefore, now.toInstant());
        userHasNotification = notificationsToSend.stream()
                .map(NotificationRequired::username)
                .anyMatch(name -> name.equals(username));

        assertEquals(false, userHasNotification);
    }

}
