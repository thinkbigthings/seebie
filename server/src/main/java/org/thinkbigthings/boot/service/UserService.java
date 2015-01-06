package org.thinkbigthings.boot.service;

import java.util.Date;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.domain.Role;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.dto.UserRegistration;
import org.thinkbigthings.boot.dto.UserResource;
import org.thinkbigthings.boot.repository.RoleRepository;
import org.thinkbigthings.boot.repository.UserRepository;

/**
 * 
 * This class doesn't implement UserDetailsService because if it implements an interface,
 * you can't wire it into the controller as a concrete class.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Inject
    public UserService(UserRepository ur, RoleRepository rr, PasswordEncoder enc) {
        userRepository = ur;
        roleRepository = rr;
        encoder = enc;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new EntityNotFoundException("User " + id);
        }

        return user;
    }

    /**
     * @param userId
     * @param userData the userData information to persist
     * @return the persisted userData
     */
    @Transactional
    public User updateUser(Long userId, UserResource userData) {

        User persistedUser = userRepository.findOne(userId);

        persistedUser.setDisplayName(userData.getDisplayName());
        persistedUser.setUsername(userData.getUsername());

        return userRepository.save(persistedUser);
    }

    @Transactional
    public User updateUserPassword(Long userId, String newPlainPassword) {
        User persistedUser = userRepository.findOne(userId);
        persistedUser.setPassword(encoder.encode(newPlainPassword));
        return userRepository.save(persistedUser);
    }
    
    @Transactional
    public User registerNewUser(UserRegistration registrationInfo) {

        User user = new User();
        user.setDisplayName(registrationInfo.getDisplayName());
        user.setUsername(registrationInfo.getUserName());
        user.setRegistration(new Date());
        user.setEnabled(true);
        user.getRoles().add(roleRepository.findByName(Role.NAME.USER));
        user.setPassword(encoder.encode(registrationInfo.getPlaintextPassword()));

        User persistedUser = userRepository.save(user);
        return persistedUser;
    }

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
