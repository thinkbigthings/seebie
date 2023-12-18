package com.seebie.server.service;

import com.seebie.server.controller.ChallengeController;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.ChallengeRepository;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import static com.seebie.server.test.data.TestData.createRandomChallenge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChallengeServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private ChallengeController challengeController;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UnsavedSleepListMapper sleepListMapper;


    @Test
    public void testTimeOverlap() {
    }

    @Test
    public void testChallengeUniqueNamePerUser() {

        var registration = TestData.createRandomUserRegistration();
        String username = registration.username();
        userService.saveNewUser(registration);

        var challenge = createRandomChallenge(-1, 14);

        challengeService.saveNewChallenge(challenge, username);

        var exception = assertThrows(DataIntegrityViolationException.class, () -> challengeService.saveNewChallenge(challenge, username));
        assertEquals("challenge_user_id_name_key", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testChallengeNameAcrossUsers() {

        String username1 = saveNewUser();
        String username2 = saveNewUser();

        var challenge = createRandomChallenge(-1, 14);

        // if the constraint is working, this is the happy path: name can be used across users
        // but in the UI it is a unique identifier, so it should be unique per user
        challengeService.saveNewChallenge(challenge, username1);
        challengeService.saveNewChallenge(challenge, username2);
    }

    private String saveNewUser() {
        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        return registration.username();
    }
}
