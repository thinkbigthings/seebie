package com.seebie.server;

import com.seebie.server.entity.SearchConfig;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.seebie.dto.RegistrationRequest;
import com.seebie.server.repository.SearchConfigRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SearchConfigRepositoryTest extends IntegrationTest {

    @Autowired
    private SearchConfigRepository searchConfigRepository;

    private static String username;

    @BeforeAll
    public static void createTestData(@Autowired UserService userService) {

        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();

        userService.saveNewUser(testUserRegistration);

        username = testUserRegistration.username();
    }


    @Test
    public void testNPlusOneWithJoinFetch() {

        Optional<SearchConfig> config = searchConfigRepository.findByUsername(username);

        assertTrue(config.isPresent());
    }

}
