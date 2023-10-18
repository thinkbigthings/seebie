package com.seebie.server.service;

import com.seebie.server.controller.SleepController;
import com.seebie.server.dto.*;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.SleepRepository;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;

import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static com.seebie.server.test.data.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

class SleepServiceIntegrationTest extends IntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SleepServiceIntegrationTest.class);

    @Autowired
    private SleepController sleepController;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private SleepRepository sleepRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UnsavedSleepListMapper sleepListMapper;


    private final PageRequest firstPage = PageRequest.of(0, 10);

    @Test
    public void testDbCalculationConstraint() throws NoSuchFieldException, IllegalAccessException {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        var oneHour = createStandardSleepData(ZonedDateTime.now(), ZonedDateTime.now().minusHours(1));
        var badCalculation = sleepListMapper.toUnsavedEntity(username, oneHour);

        // use reflection to hack our way into setting bad data
        // setter is missing because it should be hard to do the wrong thing
        Field minutesAsleepField = SleepSession.class.getDeclaredField("minutesAsleep");
        minutesAsleepField.setAccessible(true);
        minutesAsleepField.set(badCalculation, 15);

        var exception = assertThrows(DataIntegrityViolationException.class, () -> sleepRepository.save(badCalculation));
        assertEquals("correct_calculation", ((ConstraintViolationException)exception.getCause()).getConstraintName());

    }

    @Test
    public void testDbTimeOrderConstraint() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        // test with the start and stop times switched
        var badData = createStandardSleepData(ZonedDateTime.now(), ZonedDateTime.now().minusHours(1));

        var exception = assertThrows(DataIntegrityViolationException.class, () -> sleepService.saveNew(username, badData));
        assertEquals("stop_after_start", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testDbTimezoneConstraint() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        var now = ZonedDateTime.now();
        var data = createStandardSleepData(now.minusHours(1), now);
        var badTimezone = sleepListMapper.toUnsavedEntity(username, data);

        badTimezone.setSleepData(60, "", new HashSet<>(), data.startTime(), data.stopTime(), "nowhere/badZone");

        var exception = assertThrows(DataIntegrityViolationException.class, () -> sleepRepository.save(badTimezone));
        assertEquals("sleep_session_zone_id_fkey", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testRetrieveAndUpdate() {

        var registration = TestData.createRandomUserRegistration("phoenix-user");
        String username = registration.username();
        userService.saveNewUser(registration);

        var end = ZonedDateTime.now();
        var start = end.minusHours(8);

        var originalSleep = new SleepData("", 0, start, end,"America/Phoenix" );
        var savedSleep = sleepService.saveNew(username, originalSleep);

        // test retrieve
        SleepData found = sleepService.retrieve(username, savedSleep.id());

        assertEquals(originalSleep, found);

        // test update
        var updatedSleep = new SleepData("new notes", 10, start, end, "America/Phoenix");
        sleepService.update(username, savedSleep.id(), updatedSleep);
        found = sleepService.retrieve(username, savedSleep.id());
        assertEquals(updatedSleep, found);
    }

    @Test
    public void testDelete() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        // preconditions
        Page<SleepDetails> listing = sleepService.listSleepData(username, firstPage);
        assertEquals(0, listing.getTotalElements());

        // set up test data
        var sleep = sleepService.saveNew(username, createRandomSleepData());
        listing = sleepService.listSleepData(username, firstPage);
        assertEquals(1, listing.getTotalElements());

        // perform testable action
        sleepService.remove(username, sleep.id());

        // postconditions
        listing = sleepService.listSleepData(username, firstPage);
        assertEquals(0, listing.getTotalElements());
    }

    @Test
    public void testHeavyUser() {

        String username = "heavyUser";
        userService.saveNewUser(new RegistrationRequest(username, "password", "heavyUser@sleepy.com"));

        int listCount = 3000;
        var newData = createCsv(listCount, AMERICA_NEW_YORK);

        // batching means statements are sent to the DB in a batch, not that there is a single insert statement.
        // so it's expected that we see a ton of insert statements.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        sleepService.saveCsv(username, newData);
        stopWatch.stop();

        double importSeconds = stopWatch.getTotalTimeSeconds();
        LOG.info("Import time for " + listCount + " records was " + importSeconds + " seconds.");
        assertThat("import time", importSeconds, lessThan(4d));

        Page<SleepDetails> listing = sleepService.listSleepData(username, firstPage);

        assertEquals(firstPage.getPageSize(), listing.getNumberOfElements());
        assertEquals(listCount, listing.getTotalElements());
    }

    @Test
    public void testChartData() {

        String username = "chartUser";
        userService.saveNewUser(new RegistrationRequest(username, "password", "chartUser@sleepy.com"));

        int listCount = 10;
        var data = TestData.createRandomSleepData(listCount, AMERICA_NEW_YORK);
        data.forEach(d -> sleepService.saveNew(username, d));

        var to = data.getFirst().stopTime().plusDays(20);
        var from = data.getFirst().stopTime().minusDays(20);

        var points = sleepService.listChartData(username, from, to);
        assertEquals(10, points.size());
    }

    @Test
    public void testHistogram() {

        String username = "histogramUser";
        userService.saveNewUser(new RegistrationRequest(username, "password", "histoUser@sleepy.com"));

        int listCount = 10;
        var data = TestData.createRandomSleepData(listCount, AMERICA_NEW_YORK);
        data.forEach(d -> sleepService.saveNew(username, d));

        var to = data.getFirst().stopTime().plusDays(20);
        var from = data.getFirst().stopTime().minusDays(20);

        var range = new DateRange(from, to);
        var histData = sleepService.listSleepAmounts(username, new FilterList(List.of(range, range, range)));

        assertEquals(3, histData.size());
        histData.forEach(d -> assertEquals(listCount, d.size()));

    }

    @Test
    public void testZoneIdsInDbAreParsable() {
        var zones = sleepRepository.findTimezoneIds().stream().map(ZoneId::of).toList();
        assertEquals(3, zones.size());
    }

    @Test
    public void testDownloadWithTimezone() {

        var registration = TestData.createRandomUserRegistration();
        String user1 = registration.username();
        userService.saveNewUser(registration);

        registration = TestData.createRandomUserRegistration();
        String user2 = registration.username();
        userService.saveNewUser(registration);

        sleepService.saveCsv(user1, createCsv(3, AMERICA_NEW_YORK));
        var retrievedCsv1 = sleepService.retrieveCsv(user1);

        sleepService.saveCsv(user2, retrievedCsv1);
        var retrievedCsv2 = sleepService.retrieveCsv(user2);

        // after an export, import, and re-export: the two exports should be identical
        assertEquals(retrievedCsv1, retrievedCsv2);
        assertTrue(retrievedCsv1.contains(AMERICA_NEW_YORK));
    }

}
