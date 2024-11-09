package com.seebie.server.security;

import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AppUserDetailsServiceTest {

    private AppUserDetailsService service;

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private String savedUsername = TestData.createRandomUserRegistration().username();
    private User savedUser = new User(savedUsername, savedUsername, "email", "encryptedpw");

    @BeforeEach
    public void setup() {

        service = new AppUserDetailsService(userRepo);

        when(userRepo.loadUserWithRoles(eq(savedUsername))).thenReturn(of(savedUser));
    }

    @Test
    public void testUserHasNoRoles() {

        savedUser.getRoles().clear();

        assertTrue(savedUser.getRoles().isEmpty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(savedUsername));
    }

    @Test
    public void testUserHasRoles() {

        assertFalse(savedUser.getRoles().isEmpty());
        assertDoesNotThrow(() -> service.loadUserByUsername(savedUsername));
    }
}
