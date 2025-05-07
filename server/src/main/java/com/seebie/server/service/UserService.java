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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Supplier;


@Service
public class UserService {

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
    public void updatePassword(UUID publicId, String newPassword) {
        var user = userRepo.findByPublicId(publicId).orElseThrow(notFound(publicId));
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public com.seebie.server.dto.User updateUser(UUID publicId, PersonalInfo userData) {

        var user = userRepo.findByPublicId(publicId).orElseThrow(notFound(publicId));
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
    public com.seebie.server.dto.User getUser(UUID publicId) {
        return userRepo.findByPublicId(publicId).map(toUserRecord).orElseThrow(notFound(publicId));
    }

    @Transactional(readOnly = true)
    public com.seebie.server.dto.User getUserByEmail(String email) {

        return userRepo.findByEmail(email)
                .map(toUserRecord)
                .orElseThrow(() -> new EntityNotFoundException("no user found for " + email));
    }

    public static Supplier<EntityNotFoundException> notFound(UUID publicId) {
        return () -> new EntityNotFoundException("No user found: " + publicId);
    }
}
