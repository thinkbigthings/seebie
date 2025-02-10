package com.seebie.server.service;

import com.seebie.server.dto.DateRange;
import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;

import java.time.LocalDateTime;
import java.util.List;

import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.TestData.createStandardSleepData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private SleepService sleepService;

    @Autowired
    private UserService userService;

    private final PageRequest firstPage = PageRequest.of(0, 10);

    @Test
    public void testDbTimeOrderConstraint() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        String publicId = userService.getUserByEmail(registration.email()).publicId();

        // test with the start and stop times switched
        var badData = createStandardSleepData(LocalDateTime.now(), LocalDateTime.now().minusHours(1));

        var exception = assertThrows(DataIntegrityViolationException.class, () -> sleepService.saveNew(publicId, badData));
        assertEquals("stop_after_start", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testRetrieveAndUpdate() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        String publicId = userService.getUserByEmail(registration.email()).publicId();

        var end = LocalDateTime.now();
        var start = end.minusHours(8);

        var originalSleep = new SleepData("", 0, start, end,"America/Phoenix" );
        var savedSleep = sleepService.saveNew(publicId, originalSleep);

        // test retrieve
        SleepDetails found = sleepService.retrieve(publicId, savedSleep.id());

        assertEquals(originalSleep, found.sleepData());

        // test update
        var updatedSleep = new SleepData("new notes", 10, start, end, "America/Phoenix");
        sleepService.update(publicId, savedSleep.id(), updatedSleep);
        found = sleepService.retrieve(publicId, savedSleep.id());
        assertEquals(updatedSleep, found.sleepData());
    }

    @Test
    public void testDelete() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        String publicId = userService.getUserByEmail(registration.email()).publicId();

        // preconditions
        PagedModel<SleepDetails> listing = sleepService.listSleepData(publicId, firstPage);
        assertEquals(0, requireNonNull(listing.getMetadata()).totalElements());

        // set up test data
        var sleep = sleepService.saveNew(publicId, createRandomSleepData());
        listing = sleepService.listSleepData(publicId, firstPage);
        assertEquals(1, requireNonNull(listing.getMetadata()).totalElements());

        // perform testable action
        sleepService.remove(publicId, sleep.id());

        // postconditions
        listing = sleepService.listSleepData(publicId, firstPage);
        assertEquals(0, requireNonNull(listing.getMetadata()).totalElements());
    }

    @Test
    public void testChartData() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        String publicId = userService.getUserByEmail(registration.email()).publicId();

        int listCount = 10;
        var data = TestData.createRandomSleepData(listCount, AMERICA_NEW_YORK);
        data.forEach(d -> sleepService.saveNew(publicId, d));

        var to = data.getFirst().stopTime().plusDays(20);
        var from = data.getFirst().stopTime().minusDays(20);

        var points = sleepService.listChartData(publicId, from.toLocalDate(), to.toLocalDate());
        assertEquals(10, points.size());
    }

    @Test
    public void testHistogram() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        String publicId = userService.getUserByEmail(registration.email()).publicId();

        int listCount = 10;
        var data = TestData.createRandomSleepData(listCount, AMERICA_NEW_YORK);
        data.forEach(d -> sleepService.saveNew(publicId, d));

        var to = data.getFirst().stopTime().plusDays(20);
        var from = data.getFirst().stopTime().minusDays(20);

        var range = new DateRange(from.toLocalDate(), to.toLocalDate());
        var histData = sleepService.listSleepAmounts(publicId, List.of(range, range, range));

        assertEquals(3, histData.size());
        histData.forEach(durations -> assertEquals(listCount, durations.size()));

    }

}
