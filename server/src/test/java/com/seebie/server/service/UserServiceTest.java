package com.seebie.server.service;

import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.entity.Notification;
import com.seebie.server.entity.User;
import com.seebie.server.repository.NotificationRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    private PasswordEncoder pwEncoder = Mockito.mock(PasswordEncoder.class);

    private String savedUsername = "saved-user";
    private String noSuchUsername = "no-such-user";
    private User savedUser = new User(savedUsername, savedUsername, "email", "encryptedpw");
    private Notification notification = new Notification(savedUser);
    private String strongPasswordHash = "strongencryptedpasswordhere";

    private UserService service;

    @BeforeEach
    public void setup() {

        service = new UserService(userRepo, notificationRepo, pwEncoder);

        when(userRepo.findByUsername((noSuchUsername))).thenReturn(Optional.empty());

        when(userRepo.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.saveAndFlush(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.findByUsername(eq(savedUser.getUsername()))).thenReturn(of(savedUser));
        when(pwEncoder.encode(ArgumentMatchers.any(String.class))).thenReturn(strongPasswordHash);
        when(notificationRepo.findBy(eq(savedUser.getUsername()))).thenReturn(of(notification));
    }

    @Test
    public void updateUser() {

        var updateInfo = new PersonalInfo("update@email.com", savedUsername+"1");

        var updatedUser = service.updateUser(savedUsername, updateInfo);

        assertEquals(updateInfo, updatedUser.personalInfo());
    }

    @Test
    public void updateUserNotFound() {

        PersonalInfo updateInfo = new PersonalInfo("update@email.com", savedUsername+"1");

        assertThrows(EntityNotFoundException.class,
                () -> service.updateUser(noSuchUsername, updateInfo));
    }

    @Test
    public void getUser() {

        var foundUser = service.getUser(savedUsername);

        assertEquals(savedUser.getUsername(), foundUser.username());
        assertEquals(savedUser.getDisplayName(), foundUser.personalInfo().displayName());
        assertEquals(savedUser.getEmail(), foundUser.personalInfo().email());
    }

    @Test
    public void updatePasswordUserNotFound() {

        assertThrows(EntityNotFoundException.class,
                () -> service.updatePassword(noSuchUsername, "newpassword"));
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

        assertThrows(IllegalArgumentException.class, () -> service.saveNewUser(register));
    }

}
