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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // if not found, UserDetailsService is supposed to throw UsernameNotFoundException instead of return null
        UserDetails userDetails = userRepository.loadUserWithRoles(username)
                .map(toUserDetails)
                .filter(user -> ! user.getAuthorities().isEmpty())
                .orElseThrow(() -> new UsernameNotFoundException("User not found or had no authorities: " + username));

        return userDetails;
    }

}
