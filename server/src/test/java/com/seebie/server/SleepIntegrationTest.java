package com.seebie.server;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepIntegrationTest extends IntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(SleepIntegrationTest.class);

    @Autowired
    private SleepService sleepService;

    @Autowired
    private UserService userService;

    @Test
    public void testListSleep() {

        String username = "testListSleep";

        userService.saveNewUser(new RegistrationRequest(username, "password", "x@y"));

        SleepData sleep = new SleepData();

        int listCount = 40;

        for(int i=0; i < listCount; i++) {
            sleep = decrementDay(sleep);
            sleepService.saveNew(username, sleep);
        }

        int pageSize = 10;
        Page<SleepDataWithId> listing = sleepService.listSleepData(username, PageRequest.of(0, pageSize));

        assertEquals(pageSize, listing.getNumberOfElements());
        assertEquals(listCount, listing.getTotalElements());
    }

    private SleepData decrementDay(SleepData data) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(), data.startTime().minusHours(24L), data.stopTime().minusHours(24L));
    }
}
