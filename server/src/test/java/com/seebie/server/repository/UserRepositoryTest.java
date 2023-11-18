package com.seebie.server.repository;

import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;
import com.seebie.server.service.UserService;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void createTestData(@Autowired UserService userService) {
        for(int i=0; i<10; i++) {
            userService.saveNewUser(TestData.createRandomUserRegistration());
        }
    }

    /**
     * This is a demonstration of how to solve the N+1 select problem.
     * Observe the effect on the SQL (an extra query) of deleting "JOIN FETCH u.roles"
     * (put @Transactional on this test method if you do that)
     */
    @Test
    public void testNPlusOneWithJoinFetch() {

        Optional<User> admin = userRepository.loadUserWithRoles("admin");
        Set<Role> roles = admin.map(User::getRoles).get();

        assertFalse(roles.isEmpty());
        assertTrue(admin.isPresent());
    }

    @Test
    public void testListUsers() {

        long numberUsers = userRepository.count();
        Page<User> userPage = userRepository.findAll(PageRequest.of(0, 10));

        assertEquals(10, userPage.getNumberOfElements());
        assertTrue(userPage.getTotalElements() > 10);
        assertTrue(numberUsers > 10);
    }
}
