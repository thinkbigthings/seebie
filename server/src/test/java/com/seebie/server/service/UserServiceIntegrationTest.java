package com.seebie.server.service;

import com.seebie.server.repository.UserRepository;
import com.seebie.server.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final PageRequest firstPage = PageRequest.of(0, 10);

    @Test
    public void testRetrieveAndUpdate() {

        // this tests the JPQL that returns the actual data
        var users = userService.getUserSummaries(firstPage);
        assertFalse(users.getContent().isEmpty());
    }


}
