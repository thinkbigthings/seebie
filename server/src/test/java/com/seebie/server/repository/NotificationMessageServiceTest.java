package com.seebie.server.repository;

import com.seebie.server.dto.SleepData;
import com.seebie.server.service.NotificationMessageService;
import com.seebie.server.service.NotificationRequired;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.seebie.server.mapper.entitytodto.LocalDateTimeConverter.toZDT;
import static com.seebie.server.test.data.TestData.createStandardSleepData;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *
 * To debug notifications manually:
 * - Remember we can only log sleep through UI at 15m intervals, so need have thresholds longer than that for manual tests
 * - Remember to enable notifications for the test user
 * - This is a useful notification debugging query to see the critical values, just set the username to inspect:
 *      select u.username, s.stop_time, n.last_sent from notification n, sleep_session s, app_user u
 *      where u.username='admin' and s.user_id=u.id and n.user_id=u.id order by s.stop_time desc limit 1;
 */
@Execution(ExecutionMode.SAME_THREAD)
public class NotificationMessageServiceTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private NotificationMessageService notificationService;

    public interface NotificationTestArgs extends Arguments {
        default Object[] get() {
            return new Object[] { this };
        }
    }

    // What's the difference between wrapping the values here vs just having them already unwrapped in the test method?
    // Using a record allows us to name the parameters at the point of declaration instead of at the point of use.
    // It's easier to see which value is which in the IDE, and consolidates the number of arguments to the test method.
    record TestParams(boolean usernotificationEnabled, int sleepLogFrequencyHrs, int expectedNotificationCount) implements NotificationTestArgs { }

    private static List<Arguments> provideSleepLogParameters() {
        return List.of(
            new TestParams(true, 24, 0),
            new TestParams(true, 48, 1),
            new TestParams(true, 72, 2),

            new TestParams(false, 24, 0),
            new TestParams(false, 48, 0),
            new TestParams(false, 72, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSleepLogParameters")
    public void testScanForNotifications(TestParams params) {

        LocalDateTime start = LocalDateTime.now();
        SleepData firstSleepLog = createStandardSleepData(start.minusHours(8), start);
        String tz = firstSleepLog.zoneId();
        int testDurationHours = 72;

        // create new user
        var publicId = saveNewUser();

        // update notification settings
        var updatedInfo = userService.getUser(publicId)
                .personalInfo()
                .withNotificationEnabled(params.usernotificationEnabled());

        userService.updateUser(publicId, updatedInfo);

        // save first sleep session
        var firstSleepData = sleepService.saveNew(publicId, firstSleepLog).sleepData();

        long numNotifications = 0L;
        for(int hoursPassed = 0; hoursPassed < testDurationHours; hoursPassed++) {

            if(hoursPassed % params.sleepLogFrequencyHrs() == 0) {
                var nextSleep = TestData.increment(firstSleepData, Duration.ofHours(hoursPassed));
                sleepService.saveNew(publicId, nextSleep);
            }

            var present = start.plusHours(hoursPassed);
            var notificationsToSend = notificationService.findUsersToNotify(toZDT(present, tz).toInstant());

            numNotifications += notificationsToSend.stream()
                    .map(NotificationRequired::publicId)
                    .filter(publicId::equals)
                    .count();
        }

        assertEquals(params.expectedNotificationCount(), numNotifications);
    }

}
