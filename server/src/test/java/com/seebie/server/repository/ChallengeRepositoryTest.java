package com.seebie.server.repository;

import com.seebie.server.dto.Challenge;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeMapper;
import com.seebie.server.service.ChallengeService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.seebie.server.test.data.TestData.createRandomChallenge;
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

    private UnsavedChallengeMapper toEntity = new UnsavedChallengeMapper();

    @Test
    public void testListChallenges() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        var userEntity = userRepository.findByUsername(registration.username()).get();

        var current = createRandomChallenge(-1, 14);
        var completed = List.of(createRandomChallenge(-16, 14),
                                createRandomChallenge(-31, 14),
                                createRandomChallenge(-46, 14));
        var upcoming = List.of(createRandomChallenge(15, 14),
                                createRandomChallenge(30, 14));

        var dtos = new ArrayList<Challenge>();
        dtos.addAll(completed);
        dtos.addAll(upcoming);
        dtos.add(current);

        dtos.stream()
                .map(dto -> toEntity.apply(userEntity, dto))
                .forEach(challengeRepository::save);

        var today = LocalDate.now(ZoneId.of(AMERICA_NEW_YORK));
        var saved = challengeService.getChallenges(registration.username(), today);

        assertEquals(upcoming.size(), saved.upcoming().size());
        assertEquals(completed.size(), saved.completed().size());
        assertEquals(1, saved.current().size());
        assertEquals(current.name(), saved.current().getFirst().name());
    }
}
