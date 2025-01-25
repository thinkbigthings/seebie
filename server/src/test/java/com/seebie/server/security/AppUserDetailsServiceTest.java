package com.seebie.server.security;

import com.seebie.server.dto.RegistrationRequest;
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

    private final UserRepository userRepo = Mockito.mock(UserRepository.class);
    private final RegistrationRequest reg = TestData.createRandomUserRegistration();
    private final User savedUser = new User(reg.displayName(), reg.email(), "encryptedpw");

    @BeforeEach
    public void setup() {

        service = new AppUserDetailsService(userRepo);

        when(userRepo.loadUserWithRoles(eq(reg.email()))).thenReturn(of(savedUser));
    }

    @Test
    public void testUserHasNoRoles() {

        savedUser.getRoles().clear();

        assertTrue(savedUser.getRoles().isEmpty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(reg.email()));
    }

    @Test
    public void testUserHasRoles() {

        assertFalse(savedUser.getRoles().isEmpty());
        assertDoesNotThrow(() -> service.loadUserByUsername(reg.email()));
    }
}
