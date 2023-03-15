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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.*;

import static com.seebie.server.test.data.TestData.createSleepData;
import static org.junit.jupiter.api.Assertions.*;

class SleepIntegrationTest extends IntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(SleepIntegrationTest.class);

    @Autowired
    private SleepService sleepService;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    private PageRequest firstPage = PageRequest.of(0, 10);

    @Test
    public void testConstraint() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        var badData = new SleepData(ZonedDateTime.now(), ZonedDateTime.now().minusHours(1));

        assertThrows(DataIntegrityViolationException.class, () -> sleepService.saveNew(username, badData));
    }

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

        int listCount = 1000;
        var newData = createSleepData(listCount);
        sleepService.saveNew(username, newData);

        Page<SleepDataWithId> listing = sleepService.listSleepData(username, firstPage);

        assertEquals(firstPage.getPageSize(), listing.getNumberOfElements());
        assertEquals(listCount, listing.getTotalElements());

    }

    @Test
    public void testChartFormat() throws Exception {

        var mapper = converter.getObjectMapper().writerFor(SleepDataPoint.class);

        var point = new SleepDataPoint(ZonedDateTime.now(), 100);

        var s = mapper.writeValueAsString(point);

        assertNotNull(s);

    }


}
