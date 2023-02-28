package com.seebie.server;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepIntegrationTest extends IntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(SleepIntegrationTest.class);

    @Autowired
    private SleepService sleepService;

    @Autowired
    private UserService userService;

    private PageRequest firstPage = PageRequest.of(0, 10);

    @Test
    public void testDelete() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        // preconditions
        Page<SleepDataWithId> listing = sleepService.listSleepData(username, firstPage);
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
    public void testListSleep() {

        String username = "testListSleep";
        userService.saveNewUser(new RegistrationRequest(username, "password", "x@y"));

        SleepData sleep = new SleepData();

        int listCount = 40;

        ZonedDateTime latest = sleep.stopTime();
        ZonedDateTime earliest = sleep.stopTime();
        for(int i=0; i < listCount; i++) {
            sleep = decrementDay(sleep);
            earliest = sleep.stopTime();
            sleepService.saveNew(username, sleep);
        }


        Page<SleepDataWithId> listing = sleepService.listSleepData(username, firstPage);

        assertEquals(firstPage.getPageSize(), listing.getNumberOfElements());
        assertEquals(listCount, listing.getTotalElements());

        var graphingData = sleepService.listSleepPlotData(username, earliest, latest);
        System.out.println(graphingData);
    }

    private SleepData decrementDay(SleepData data) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(), data.startTime().minusHours(24L), data.stopTime().minusHours(24L));
    }
}
