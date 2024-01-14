package com.seebie.server.service;

import com.seebie.server.controller.ChallengeController;
import com.seebie.server.dto.Challenge;
import com.seebie.server.dto.ChallengeDetails;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.ChallengeRepository;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

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
    public void testRetrieveAndUpdate() {

        String username = saveNewUser();

        var end = LocalDate.now();
        var start = end.minusDays(7);

        var originalChallenge = new Challenge("Title", "", start, end);
        var savedChallenge = challengeService.saveNewChallenge(username, originalChallenge);

        // test retrieve
        Challenge found = challengeService.retrieve(username, savedChallenge.id());

        assertEquals(originalChallenge, found);

        // test update
        var updated = new Challenge("New title", "stuff", start, end);
        challengeService.update(username, savedChallenge.id(), updated);
        found = challengeService.retrieve(username, savedChallenge.id());
        assertEquals(updated, found);
    }

    @Test
    public void testDelete() {

        String username = saveNewUser();
        var today = LocalDate.now();

        // preconditions
        List<ChallengeDetails> completed = challengeService.getChallenges(username, today).completed();
        assertEquals(0, completed.size());

        // set up test data
        var challenge = challengeService.saveNewChallenge(username, createRandomChallenge(-14, 7));
        completed = challengeService.getChallenges(username, today).completed();
        assertEquals(1, completed.size());

        // perform testable action
        challengeService.remove(username, challenge.id());

        // postconditions
        completed = challengeService.getChallenges(username, today).completed();
        assertEquals(0, completed.size());
    }

    @Test
    public void testChallengeUniqueNamePerUser() {

        String username = saveNewUser();

        var challenge = createRandomChallenge(-1, 14);

        challengeService.saveNewChallenge(username, challenge);

        var exception = assertThrows(DataIntegrityViolationException.class, () -> challengeService.saveNewChallenge(username, challenge));
        assertEquals("challenge_user_id_name_key", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testConstraintStopAfterStart() {

        var username = saveNewUser();

        // test with the start and stop times switched
        var badData = new Challenge("title", "", LocalDate.now(), LocalDate.now().minusDays(7L));

        var exception = assertThrows(DataIntegrityViolationException.class, () -> challengeService.saveNewChallenge(username, badData));
        assertEquals("stop_after_start", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testChallengeNameAcrossUsers() {

        String username1 = saveNewUser();
        String username2 = saveNewUser();

        var challenge = createRandomChallenge(-1, 14);

        // if the constraint is working, this is the happy path: name can be used across users
        // but in the UI it is a unique identifier, so it should be unique per user
        challengeService.saveNewChallenge(username1, challenge);
        challengeService.saveNewChallenge(username2, challenge);
    }

    private String saveNewUser() {
        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        return registration.username();
    }
}
