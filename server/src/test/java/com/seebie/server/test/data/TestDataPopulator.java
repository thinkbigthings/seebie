package com.seebie.server.test.data;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;

public class TestDataPopulator implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final SleepService sleepService;
    private boolean alreadySetup = false;

    public TestDataPopulator(UserService userService, SleepService sleepService) {
        this.userService = userService;
        this.sleepService = sleepService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Prevent running multiple times in case of multiple context refreshes
        if (!alreadySetup) {
            populateTestData();
            alreadySetup = true;
        }
    }

    private void populateTestData() {
        String username = "asdf";
        userService.saveNewUser(new RegistrationRequest(username, "asdf", "staticTestUser@seebie.com"));
        createRandomSleepData(60, AMERICA_NEW_YORK).forEach(d -> sleepService.saveNew(username, d));
    }

}