package com.seebie.server.repository;

import com.seebie.server.entity.Challenge;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.seebie.server.test.data.TestData.createRandomChallenges;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChallengeRepositoryTest extends IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChallengeRepository challengeRepository;


    @Test
    public void testListChallenges() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        var userEntity = userRepository.findByUsername(registration.username()).get();

        var challenges = createRandomChallenges(10);
        var entities = challenges.stream()
                .map(dto -> new Challenge(dto.name(), dto.description(), dto.start(), dto.finish(), userEntity))
                .toList();

        challengeRepository.saveAll(entities);

        var saved = challengeRepository.findAllByUsername(registration.username());
        assertEquals(10, saved.size());
    }
}
