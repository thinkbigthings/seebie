package com.seebie.server.service;

import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.dto.ChallengeDto;
import com.seebie.server.test.IntegrationTest;
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
    private ChallengeService challengeService;

    @Test
    public void testRetrieveAndUpdate() {

        var publicId = saveNewUser();

        var end = LocalDate.now();
        var start = end.minusDays(7);

        var originalChallenge = new ChallengeDto("Title", "", start, end);
        var savedChallenge = challengeService.saveNew(publicId, originalChallenge);

        // test retrieve
        var found = challengeService.retrieve(publicId, savedChallenge.id());

        assertEquals(originalChallenge, found.challenge());

        // test update
        var updated = new ChallengeDto("New title", "stuff", start, end);
        challengeService.update(publicId, savedChallenge.id(), updated);
        found = challengeService.retrieve(publicId, savedChallenge.id());
        assertEquals(updated, found.challenge());
    }

    @Test
    public void testDelete() {

        var publicId = saveNewUser();

        // preconditions
        List<ChallengeDetailDto> completed = challengeService.getChallenges(publicId);
        assertEquals(0, completed.size());

        // set up test data
        var challenge = challengeService.saveNew(publicId, createRandomChallenge(-14, 7));
        completed = challengeService.getChallenges(publicId);
        assertEquals(1, completed.size());

        // perform testable action
        challengeService.remove(publicId, challenge.id());

        // postconditions
        completed = challengeService.getChallenges(publicId);
        assertEquals(0, completed.size());
    }

    @Test
    public void testChallengeUniqueNamePerUser() {

        var publicId = saveNewUser();

        var challenge = createRandomChallenge(-1, 14);

        challengeService.saveNew(publicId, challenge);

        var exception = assertThrows(DataIntegrityViolationException.class, () -> challengeService.saveNew(publicId, challenge));
        assertEquals("challenge_user_id_name_key", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testConstraintStopAfterStart() {

        var publicId = saveNewUser();

        // test with the start and stop times switched
        var badData = new ChallengeDto("title", "", LocalDate.now(), LocalDate.now().minusDays(7L));

        var exception = assertThrows(DataIntegrityViolationException.class, () -> challengeService.saveNew(publicId, badData));
        assertEquals("stop_after_start", ((ConstraintViolationException)exception.getCause()).getConstraintName());
    }

    @Test
    public void testChallengeNameAcrossUsers() {

        var publicId1 = saveNewUser();
        var publicId2 = saveNewUser();

        var challenge = createRandomChallenge(-1, 14);

        // if the constraint is working, this is the happy path: name can be used across users
        // but in the UI it is a unique identifier, so it should be unique per user
        challengeService.saveNew(publicId1, challenge);
        challengeService.saveNew(publicId2, challenge);
    }

}
