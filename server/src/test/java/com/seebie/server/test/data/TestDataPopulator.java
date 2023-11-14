package com.seebie.server.test.data;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import org.springframework.boot.CommandLineRunner;

import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;

public class TestDataPopulator implements CommandLineRunner {

    private UserService userService;
    private SleepService sleepService;

    public TestDataPopulator(UserService userService, SleepService sleepService) {
        this.userService = userService;
        this.sleepService = sleepService;
    }

    @Override
    public void run(String... args) {

        String username = "ui";
        userService.saveNewUser(new RegistrationRequest(username, "ui", "staticTestUser@seebie.com"));

        createRandomSleepData(60, AMERICA_NEW_YORK).forEach(d -> sleepService.saveNew(username, d));
    }
}
