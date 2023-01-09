package com.seebie.server;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.Sleep;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleepIntegrationTest extends IntegrationTest {

    @Autowired
    private SleepService sleepService;

    @Autowired
    private UserService userService;

    @Test
    public void testListSleep() {

        RegistrationRequest registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);

        Sleep lastNight = new Sleep(LocalDate.now(), 450);
        String username = registration.username();

        sleepService.save(username, lastNight);

        Page<Sleep> listing = sleepService.listSleepData(username, PageRequest.of(0, 10));

        assertEquals(1, listing.getNumberOfElements());
        assertEquals(1, listing.getTotalElements());


    }
}
