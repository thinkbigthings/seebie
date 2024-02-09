package com.seebie.server.repository;

import com.seebie.server.mapper.dtotoentity.UnsavedChallengeListMapper;
import com.seebie.server.service.ChallengeService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.seebie.server.test.data.TestData.createRandomChallenges;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChallengeRepositoryTest extends IntegrationTest {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UnsavedChallengeListMapper toEntity;

    @Test
    public void testListChallenges() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);

        createRandomChallenges().stream()
                .map(dto -> toEntity.toUnsavedEntity(registration.username(), dto))
                .forEach(challengeRepository::save);

        var today = LocalDate.now(ZoneId.of(AMERICA_NEW_YORK));
        var saved = challengeService.getChallenges(registration.username(), today);

        assertEquals(2, saved.upcoming().size());
        assertEquals(3, saved.completed().size());
        assertEquals(1, saved.current().size());
    }
}
