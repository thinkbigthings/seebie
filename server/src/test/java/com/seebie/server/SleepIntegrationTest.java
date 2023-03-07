package com.seebie.server;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataPoint;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.repository.SleepRepository;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SleepIntegrationTest extends IntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(SleepIntegrationTest.class);

    @Autowired
    private SleepService sleepService;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    private PageRequest firstPage = PageRequest.of(0, 10);

    private Random random = new Random();

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

        SleepData today = new SleepData();

        int listCount = 1000;

        ZonedDateTime latest = today.stopTime();
        ZonedDateTime earliest = today.stopTime();
        List<SleepData> newData = new ArrayList<>();
        for(int i=0; i < listCount; i++) {
            SleepData session = decrementDays(today, i);
            session = randomizeDuration(session);
            earliest = session.stopTime();
            newData.add(session);
        }

        sleepService.saveNew(username, newData);

        Page<SleepDataWithId> listing = sleepService.listSleepData(username, firstPage);

        assertEquals(firstPage.getPageSize(), listing.getNumberOfElements());
        assertEquals(listCount, listing.getTotalElements());



        // CHART TESTS

        var graphingData = sleepService.listChartData(username, earliest, latest);
        // System.out.println(graphingData);
    }

    @Test
    public void testChartFormat() throws Exception {

        var mapper = converter.getObjectMapper().writerFor(SleepDataPoint.class);

        var point = new SleepDataPoint(ZonedDateTime.now(), 100);

        var s = mapper.writeValueAsString(point);

        assertNotNull(s);

    }

    private SleepData decrementDays(SleepData data, long days) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(),
                data.startTime().minusDays(days),
                data.stopTime().minusDays(days));
    }

    private SleepData randomizeDuration(SleepData data) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(),
                data.startTime().plusMinutes(random.nextInt(60)),
                data.stopTime().minusMinutes(random.nextInt(60)));
    }
}
