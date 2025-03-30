package com.seebie.server.service;

import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.entity.Notification;
import com.seebie.server.entity.User;
import com.seebie.server.repository.NotificationRepository;
import com.seebie.server.repository.UserRepository;
import com.seebie.server.test.data.TestData;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    private PasswordEncoder pwEncoder = Mockito.mock(PasswordEncoder.class);

    private RegistrationRequest reg = TestData.createRandomUserRegistration();
    private UUID savedUserPublicId = UUID.randomUUID();
    private UUID noSuchUserPublicId = UUID.randomUUID();
    private User savedUser = new User(reg.displayName(), reg.email(), "encryptedpw");
    private Notification notification = new Notification(savedUser);
    private String strongPasswordHash = "strongencryptedpasswordhere";

    private UserService service;

    @BeforeEach
    public void setup() throws Exception {

        service = new UserService(userRepo, notificationRepo, pwEncoder);

        // use reflection to set publicId since we don't want to add a setter for generated values
        Field field = savedUser.getClass().getDeclaredField("publicId");
        field.setAccessible(true);
        field.set(savedUser, savedUserPublicId);

        when(userRepo.findByPublicId((noSuchUserPublicId))).thenReturn(Optional.empty());

        when(userRepo.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.findByPublicId(eq(savedUserPublicId))).thenReturn(of(savedUser));
        when(pwEncoder.encode(ArgumentMatchers.any(String.class))).thenReturn(strongPasswordHash);
        when(notificationRepo.findBy(eq(savedUserPublicId))).thenReturn(of(notification));
    }

    @Test
    public void updateUser() {

        var updateInfo = new PersonalInfo(savedUser.getDisplayName()+"1", true);

        var updatedUser = service.updateUser(savedUserPublicId, updateInfo);

        assertEquals(updateInfo, updatedUser.personalInfo());
    }

    @Test
    public void updateUserNotFound() {

        var updateInfo = new PersonalInfo(savedUser.getDisplayName()+"1", true);

        assertThrows(EntityNotFoundException.class,
                () -> service.updateUser(noSuchUserPublicId, updateInfo));
    }

    @Test
    public void getUser() {

        var foundUser = service.getUser(savedUserPublicId);

        assertEquals(savedUser.getPublicId(), foundUser.publicId());
        assertEquals(savedUser.getDisplayName(), foundUser.personalInfo().displayName());
        assertEquals(savedUser.getEmail(), foundUser.email());
    }

    @Test
    public void updatePasswordUserNotFound() {

        assertThrows(EntityNotFoundException.class,
                () -> service.updatePassword(noSuchUserPublicId, "newpassword"));
    }

    @Test
    public void updatePassword() {

        service.updatePassword(savedUserPublicId, "newpassword");

        assertEquals(strongPasswordHash, savedUser.getPassword());
    }

    @Test
    public void blockDuplicateEmail() {

        when(userRepo.existsByEmail(ArgumentMatchers.any(String.class))).thenReturn(true);

        RegistrationRequest register = new RegistrationRequest("name", "b", "name@email.com");

        assertThrows(IllegalArgumentException.class, () -> service.saveNewUser(register));
    }

}
