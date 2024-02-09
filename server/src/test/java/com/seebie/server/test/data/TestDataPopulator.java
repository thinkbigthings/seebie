package com.seebie.server.test.data;

import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.service.ChallengeService;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import static com.seebie.server.test.data.TestData.createRandomChallenge;
import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;

public class TestDataPopulator implements ApplicationListener<ContextRefreshedEvent> {

    private final UserService userService;
    private final SleepService sleepService;
    private final ChallengeService challengeService;

    private boolean alreadySetup = false;

    public TestDataPopulator(UserService userService, SleepService sleepService, ChallengeService challengeService) {
        this.userService = userService;
        this.sleepService = sleepService;
        this.challengeService = challengeService;
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

        String username = "test";

        userService.saveNewUser(new RegistrationRequest(username, "test", "staticTestUser@seebie.com"));

        createRandomSleepData(60, AMERICA_NEW_YORK).forEach(d -> sleepService.saveNew(username, d));

        challengeService.saveNew(username, createRandomChallenge(-90, 14));
        challengeService.saveNew(username, createRandomChallenge(-60, 14));
        challengeService.saveNew(username, createRandomChallenge(-30, 14));
        challengeService.saveNew(username, createRandomChallenge(-1, 14));
        challengeService.saveNew(username, createRandomChallenge(30, 14));
    }

}
