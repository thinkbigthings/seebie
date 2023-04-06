package com.seebie.server.service;

import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.UserSummary;
import com.seebie.server.entity.Notification;
import com.seebie.server.mapper.entitytodto.UserMapper;
import com.seebie.server.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;


@Service
public class UserService {

    private static Logger LOG = LoggerFactory.getLogger(UserService.class);


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

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public com.seebie.server.dto.User updateUser(String username, PersonalInfo userData) {

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + username));

        user.setEmail(userData.email());
        user.setDisplayName(userData.displayName());
        user.setNotificationsEnabled(userData.notificationsEnabled());

        // If user turns it on, set last notification time to current time,
        // so they are notified at the next appropriate time.
        var notification = notificationRepo.findBy(username)
                .orElseThrow(() -> new RuntimeException("Server Error: No notification exists for " + username));
        notification.withLastSent(Instant.now());

        return toUserRecord.apply(user);
    }

    @Transactional
    public void saveNewUser(RegistrationRequest registration) {

        String username = registration.username();

        if(userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists " + registration.username());
        }
        var entity = userRepo.save(fromRegistration(registration));

        LOG.info("Saved new user with id " + entity.getId());

        notificationRepo.save(new Notification(entity));
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

    @Transactional(readOnly = true)
    public com.seebie.server.dto.User loginUser(String name) {

        // The web session isn't saved until the db is flushed at the end.
        // That's why we use withLoggedIn
        return getUser(name).withIsLoggedIn(true);
    }

    public User fromRegistration(RegistrationRequest registration) {

        var user = new User(registration.username(), registration.username());

        user.setDisplayName(registration.username());
        user.setEmail(registration.email());
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(registration.plainTextPassword()));
        user.getRoles().add(Role.USER);

        return user;
    }

}
