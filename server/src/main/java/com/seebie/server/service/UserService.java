package com.seebie.server.service;

import com.seebie.server.dto.AddressRecord;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.UserSummary;
import com.seebie.server.mapper.entitytodto.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seebie.server.entity.Address;
import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;



@Service
public class UserService {

    private static Logger LOG = LoggerFactory.getLogger(UserService.class);


    private UserMapper toUserRecord = new UserMapper();

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.userRepo = repo;
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

        List<Address> newAddressEntities = userData.addresses().stream()
                .map(this::fromRecord)
                .toList();

        user.getAddresses().clear();
        user.getAddresses().addAll(newAddressEntities);
        newAddressEntities.forEach(a -> a.setUser(user));

        return toUserRecord.apply(user);
    }

    @Transactional
    public void saveNewUser(RegistrationRequest registration) {

        String username = registration.username();

        if(userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists " + registration.username());
        }
        userRepo.save(fromRegistration(registration));
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

    public Address fromRecord(AddressRecord addressData) {

        var address = new Address();

        address.setLine1(addressData.line1());
        address.setCity(addressData.city());
        address.setState(addressData.state());
        address.setZip(addressData.zip());

        return address;
    }

}
