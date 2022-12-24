package com.seebie.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.server.ResponseStatusException;
import com.seebie.server.dto.AddressRecord;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;
import com.seebie.server.service.UserService;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private PasswordEncoder pwEncoder = Mockito.mock(PasswordEncoder.class);

    private String savedUsername = "saveduser";
    private User savedUser = new User(savedUsername, savedUsername);
    private String strongPasswordHash = "strongencryptedpasswordhere";

    private UserService service;

    @BeforeEach
    public void setup() {

        service = new UserService(userRepo, pwEncoder);

        savedUser.setRegistrationTime(Instant.now());

        when(userRepo.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.saveAndFlush(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.findByUsername(eq(savedUser.getUsername()))).thenReturn(of(savedUser));
        when(pwEncoder.encode(ArgumentMatchers.any(String.class))).thenReturn(strongPasswordHash);
    }

    @Test
    public void updateUser() {

        Set<AddressRecord> addresses = Set.of(new AddressRecord("123 A St", "Philadelphia", "PA", "19109"));
        PersonalInfo updateInfo = new PersonalInfo("update@email.com", savedUsername+"1", addresses);

        com.seebie.server.dto.User updatedUser = service.updateUser(savedUsername, updateInfo);

        assertEquals(updateInfo, updatedUser.personalInfo());
    }

    @Test
    public void getUser() {

        com.seebie.server.dto.User foundUser = service.getUser(savedUsername);

        assertEquals(savedUser.getUsername(), foundUser.username());
        assertEquals(savedUser.getDisplayName(), foundUser.personalInfo().displayName());
        assertEquals(savedUser.getEmail(), foundUser.personalInfo().email());
    }


    @Test
    public void updatePassword() {

        service.updatePassword(savedUsername, "newpassword");

        assertEquals(strongPasswordHash, savedUser.getPassword());
    }

    @Test
    public void blockDuplicateUsername() {

        when(userRepo.existsByUsername(ArgumentMatchers.any(String.class))).thenReturn(true);

        RegistrationRequest register = new RegistrationRequest("username", "b", "name@email.com");

        assertThrows(ResponseStatusException.class, () -> service.saveNewUser(register));
    }

    @Test
    public void blockConstraintViolationOnRegister() {

        when(userRepo.save(ArgumentMatchers.any(User.class))).thenThrow(new ConstraintViolationException(new HashSet<>()));

        RegistrationRequest register = new RegistrationRequest("username", "b", "name@email.com");

        assertThrows(ResponseStatusException.class, () -> service.saveNewUser(register));
    }

    @Test
    public void blockUrlUnsafeUsername() {

        String name = "first last";

        RegistrationRequest register = new RegistrationRequest(name, "b", "name@email.com");

        assertThrows(ResponseStatusException.class, () -> service.saveNewUser(register));
    }

}
