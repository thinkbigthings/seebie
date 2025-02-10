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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserMapper toUserRecord = new UserMapper();

    private final UserRepository userRepo;
    private final NotificationRepository notificationRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, NotificationRepository notificationRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = repo;
        this.notificationRepo = notificationRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updatePassword(String publicId, String newPassword) {

        userRepo.findByPublicId(publicId)
                .ifPresentOrElse(
                    user -> user.setPassword(passwordEncoder.encode(newPassword)),
                    () -> { throw new EntityNotFoundException("No user found: " + publicId); }
                );
    }

    @Transactional
    public com.seebie.server.dto.User updateUser(String publicId, PersonalInfo userData) {

        var user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("No user found: " + publicId) );

        user.withUserData(userData.displayName(), userData.notificationsEnabled());

        return toUserRecord.apply(user);
    }

    @Transactional
    public void saveNewUser(RegistrationRequest registration) {

        String email = registration.email();

        if(userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists " + registration.email());
        }

        var encryptedPassword = passwordEncoder.encode(registration.plainTextPassword());
        var unsavedUser = new User(registration.displayName(), registration.email(), encryptedPassword);

        notificationRepo.save(new Notification(unsavedUser));
    }

    @Transactional(readOnly = true)
    public PagedModel<UserSummary> getUserSummaries(Pageable page) {
        return new PagedModel<>(userRepo.loadSummaries(page));
    }

    @Transactional(readOnly = true)
    public com.seebie.server.dto.User getUser(String publicId) {

        return userRepo.findByPublicId(publicId)
                .map(toUserRecord)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + publicId));
    }

    @Transactional(readOnly = true)
    public com.seebie.server.dto.User getUserByEmail(String email) {

        return userRepo.findByEmail(email)
                .map(toUserRecord)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + email));
    }
}
