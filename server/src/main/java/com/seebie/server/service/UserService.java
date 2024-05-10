package com.seebie.server.service;

import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.UserSummary;
import com.seebie.server.entity.Notification;
import com.seebie.server.entity.User;
import com.seebie.server.mapper.entitytodto.UserMapper;
import com.seebie.server.repository.NotificationRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
public class UserService {

    private UserMapper toUserRecord = new UserMapper();

    private UserRepository userRepo;
    private NotificationRepository notificationRepo;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, NotificationRepository notificationRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = repo;
        this.notificationRepo = notificationRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updatePassword(String username, String newPassword) {

        userRepo.findByUsername(username)
                .ifPresentOrElse(
                    user -> user.setPassword(passwordEncoder.encode(newPassword)),
                    () -> { throw new EntityNotFoundException("No user found: " + username); }
                );
    }

    @Transactional
    public com.seebie.server.dto.User updateUser(String username, PersonalInfo userData) {

        var notification = notificationRepo.findBy(username)
                .orElseThrow(() -> new EntityNotFoundException("No notification exists for " + username));

        // If user turns notifications on, set last notification time to current time,
        // so they are notified at the next appropriate time.
        boolean userNotificationSwitchedOn = ! notification.getUser().isNotificationsEnabled() && userData.notificationsEnabled();
        if( userNotificationSwitchedOn ) {
            notification.withLastSent(Instant.now());
        }

        var user = notification.getUser();
        user.setUserData(userData.email(), userData.displayName(), userData.notificationsEnabled());

        return toUserRecord.apply(user);
    }

    @Transactional
    public void saveNewUser(RegistrationRequest registration) {

        String username = registration.username();

        if(userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists " + registration.username());
        }

        var encryptedPassword = passwordEncoder.encode(registration.plainTextPassword());
        var unsavedUser = new User(registration.username(), registration.username(), registration.email(), encryptedPassword);

        notificationRepo.save(new Notification(unsavedUser));
    }

    @Transactional(readOnly = true)
    public Page<UserSummary> getUserSummaries(Pageable page) {

        return userRepo.loadSummaries(page);
    }

    @Transactional(readOnly = true)
    public com.seebie.server.dto.User getUser(String username) {

        return userRepo.findByUsername(username)
                .map(toUserRecord)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));
    }

}
