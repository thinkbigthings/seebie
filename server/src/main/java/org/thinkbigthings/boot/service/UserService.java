package org.thinkbigthings.boot.service;

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
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

    // TODO 3 strip passwords when users are retrieved. 
    // should we do this at service layer because it's business logic, or at web layer since it is dealing with output/response?
    // maybe abstract that into a generic class that cleanses output, just like something that cleanses input?
    // [UPDATE] should use a mapper and only return the data you need to return.
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

        return userRepository.saveAndFlush(persistedUser);
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
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
