package com.seebie.server.security;

import com.seebie.server.mapper.entitytodto.UserDetailsMapper;
import com.seebie.server.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private UserDetailsMapper toUserDetails = new UserDetailsMapper();

    private UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @param email We'll use the email as the username for Spring Security purposes.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // if not found, UserDetailsService is supposed to throw UsernameNotFoundException instead of return null
        return userRepository.loadUserWithRoles(email)
                .map(toUserDetails)
                .filter(user -> ! user.getAuthorities().isEmpty())
                .orElseThrow(() -> new UsernameNotFoundException("User not found or had no authorities: " + email));
    }

}
