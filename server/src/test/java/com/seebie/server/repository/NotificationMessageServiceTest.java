package com.seebie.server.repository;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.service.NotificationMessageService;
import com.seebie.server.service.NotificationRequired;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
    private static final SleepData firstSleepLog = new SleepData(start.minusHours(8), start);
    private static final int testDurationHours = 72;

    // What's the difference between wrapping the values here vs just having them already unwrapped in the test method?
    // Using a record allows us to name the parameters at the point of declaration instead of at the point of use.
    record TestParameters(boolean notificationsEnabled, int logSleepEveryNumHours, int expectedNotificationCount) {}

    private static List<Arguments> provideSleepLogParameters() {
        return List.of(
                Arguments.of(new TestParameters(true, 12, 0)),
                Arguments.of(new TestParameters(true, 24, 0)),
                Arguments.of(new TestParameters(true, 48, 1)),
                Arguments.of(new TestParameters(true, 96, 2)),

                Arguments.of(new TestParameters(false, 12, 0)),
                Arguments.of(new TestParameters(false, 24, 0)),
                Arguments.of(new TestParameters(false, 48, 0)),
                Arguments.of(new TestParameters(false, 96, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("provideSleepLogParameters")
    public void testScanForNotifications(TestParameters params) {

        String userPrefix = String.join( "-", "notify",
                Boolean.toString(params.notificationsEnabled()),
                Integer.toString(params.logSleepEveryNumHours()) );

        // create new user
        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration(userPrefix);
        userService.saveNewUser(testUserRegistration);
        String username = testUserRegistration.username();

        // update notification settings
        var updatedInfo = userService.getUser(username)
                .personalInfo()
                .withNotificationEnabled(params.notificationsEnabled());

        userService.updateUser(username, updatedInfo);

        // save sleep session
        var firstSleepData = sleepService.saveNew(username, firstSleepLog).sleepData();


        long numNotifications = 0L;
        for(int hoursPassed = 0; hoursPassed < testDurationHours; hoursPassed++) {

            if(hoursPassed % params.logSleepEveryNumHours() == 0) {
                var nextSleep = TestData.increment(firstSleepData, Duration.ofHours(hoursPassed));
                sleepService.saveNew(username, nextSleep).sleepData();
            }

            var present = start.plusHours(hoursPassed);
            var notificationsToSend = notificationService.findUsersToNotify(present.toInstant());

            numNotifications += notificationsToSend.stream()
                    .map(NotificationRequired::username)
                    .filter(username::equals)
                    .count();
        }

        assertEquals(params.expectedNotificationCount(), numNotifications);
    }

}
