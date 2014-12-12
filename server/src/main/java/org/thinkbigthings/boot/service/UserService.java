package org.thinkbigthings.boot.service;

import java.util.Date;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.domain.Role;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.repository.RoleRepository;
import org.thinkbigthings.boot.repository.UserRepository;

@Service
class UserService implements UserDetailsService, UserServiceInterface {

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
    @Override
    public User getUserById(Long id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new EntityNotFoundException("User " + id);
        }

        return user;
    }

    /**
     * @param userData the userData information to persist
     * @return the persisted userData
     */
    @Transactional
    @Override
    public User updateUser(User userData) {

        User persistedUser = userRepository.findOne(userData.getId());

        persistedUser.setDisplayName(userData.getDisplayName());
        persistedUser.setUsername(userData.getUsername());

        return userRepository.save(persistedUser);
    }

    @Transactional
    @Override
    public User registerNewUser(User newUser) {

        User user = new User();
        user.setDisplayName(newUser.getDisplayName());
        user.setUsername(newUser.getUsername());
        user.setRegistration(new Date());
        user.setEnabled(true);
        user.getRoles().add(roleRepository.findByName(Role.NAME.USER));
        user.setPassword(encoder.encode(newUser.getPassword()));

        User persistedUser = userRepository.save(user);
        return persistedUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
