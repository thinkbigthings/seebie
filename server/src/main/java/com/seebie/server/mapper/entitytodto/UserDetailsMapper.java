package com.seebie.server.mapper.entitytodto;

import org.springframework.security.core.userdetails.UserDetails;
import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;

import static org.springframework.security.core.userdetails.User.builder;

import java.util.Set;
import java.util.function.Function;

public class UserDetailsMapper implements Function<User, UserDetails> {

    @Override
    public UserDetails apply(User user) {

        return builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled( ! user.isEnabled())
                .roles(toNames(user.getRoles()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    private String[] toNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::name)
                .toArray(String[]::new);
    }

}
