package com.seebie.server.repository;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.service.NotificationMessageService;
import com.seebie.server.service.NotificationRequired;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 *
 * To debug notifications manually:
 * - Set override values in the run configuration: two "trigger after" values, set notification output to LOG
 *   instead of EMAIL, and can increase the scan schedule to every minute.
 *   Can only log sleep through UI at 15m intervals, will need to have thresholds longer than that for manual tests.
 * - Remember to enable notifications for the test user
 * - This is a useful notification debugging query to see the critical values, just set the username to inspect:
 *      select u.username, s.stop_time, n.last_sent from notification n, sleep_session s, app_user u
 *      where u.username='admin' and s.user_id=u.id and n.user_id=u.id order by s.stop_time desc limit 1;
 */
public class NotificationMessageServiceTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private NotificationMessageService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private static final ZonedDateTime start = ZonedDateTime.now();
    private static final SleepData sleepToday = new SleepData(start.minusHours(8), start);


    @Test
    public void testScanForNotifications() {

        // TODO also test with notifications disabled and with missing sleep

        // TODO test with user adding sleep on time or only adding the first one

        // create new user and update notification setting
        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        String username = testUserRegistration.username();
        userService.updateUser(username, userService.getUser(username).personalInfo().withNotificationEnabled(true));

        // save sleep session
        var firstSleepData = sleepService.saveNew(username, sleepToday).sleepData();


        var notify = new ArrayList<String>();

        for(int hoursPassed = 0; hoursPassed < 96; hoursPassed++) {

            if(hoursPassed % 12 == 0) {
                var nextSleep = TestData.increment(firstSleepData, Duration.ofHours(hoursPassed));
                sleepService.saveNew(username, nextSleep).sleepData();
            }

            var present = start.plusHours(hoursPassed);
            var notificationsToSend = notificationService.findUsersToNotify(present.toInstant());

            final int hours = hoursPassed;
            notificationsToSend.stream()
                    .map(NotificationRequired::username)
                    .filter(username::equals)
                    .map(name -> "Hours Passed: " + hours + " and user has notifications.")
                    .collect(Collectors.toCollection(() -> notify));
        }

        var message = "No notifications should be sent, but have : " + System.lineSeparator();
        message += String.join(System.lineSeparator(), notify);
        assertTrue(notify.isEmpty(), message);
    }

}
