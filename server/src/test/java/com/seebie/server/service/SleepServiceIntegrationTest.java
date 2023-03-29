package com.seebie.server.service;

import com.seebie.server.controller.SleepController;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;

import static com.seebie.server.test.data.TestData.createSleepData;
import static org.junit.jupiter.api.Assertions.*;

class SleepServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private SleepController sleepController;

    @Autowired
    private SleepService sleepService;

    @Autowired
    private UserService userService;

    private PageRequest firstPage = PageRequest.of(0, 10);

    @Test
    public void testConstraint() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        // test with the start and stop times switched
        var badData = new SleepData(ZonedDateTime.now(), ZonedDateTime.now().minusHours(1));

        assertThrows(DataIntegrityViolationException.class, () -> sleepService.saveNew(username, badData));
    }


    @Disabled("Fails on Github, need to upload test output to be able to investigate")
    @Test
    public void testRetrieveAndUpdate() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        var originalSleep = new SleepData();
        var savedSleep = sleepService.saveNew(username, originalSleep);

        // test retrieve
        SleepData found = sleepService.retrieve(username, savedSleep.id());

        // this fails on github for some reason?
        assertEquals(originalSleep, found);

        // test update
        var updatedSleep = TestData.randomDuration(originalSleep);
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
        var sleep = sleepService.saveNew(username, new SleepData());
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

        int listCount = 2000;
        var newData = createSleepData(listCount);
        sleepService.saveNew(username, newData);

        Page<SleepDetails> listing = sleepService.listSleepData(username, firstPage);

        assertEquals(firstPage.getPageSize(), listing.getNumberOfElements());
        assertEquals(listCount, listing.getTotalElements());
    }

}
