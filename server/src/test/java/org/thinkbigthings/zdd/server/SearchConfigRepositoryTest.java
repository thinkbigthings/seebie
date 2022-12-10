package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.server.repository.SearchConfigRepository;
import org.thinkbigthings.zdd.server.service.UserService;
import org.thinkbigthings.zdd.server.entity.SearchConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;

class SearchConfigRepositoryTest extends IntegrationTest {

    @Autowired
    private SearchConfigRepository searchConfigRepository;

    private static String username;

    @BeforeAll
    public static void createTestData(@Autowired UserService userService) {

        RegistrationRequest testUserRegistration = createRandomUserRegistration();

        userService.saveNewUser(testUserRegistration);

        username = testUserRegistration.username();
    }


    @Test
    public void testNPlusOneWithJoinFetch() {

        Optional<SearchConfig> config = searchConfigRepository.findByUsername(username);

        assertTrue(config.isPresent());
    }

}
