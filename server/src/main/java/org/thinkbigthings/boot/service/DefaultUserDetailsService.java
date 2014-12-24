package org.thinkbigthings.boot.service;

import javax.inject.Inject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.repository.UserRepository;

@Service
public class DefaultUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Inject
    public DefaultUserDetailsService(UserRepository ur) {
        userRepository = ur;
    }
    
    /**
     * Roles are loaded in this method because they are used by the security system.
     * 
     * @param username
     * @return UserDetails for this username
     * @throws UsernameNotFoundException 
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        user.getRoles().size();
        return user;
    }

}
