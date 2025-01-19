package com.seebie.server.mapper.entitytodto;

import com.seebie.server.security.AppUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;

import static org.springframework.security.core.userdetails.User.builder;

import java.util.Set;
import java.util.function.Function;

public class UserDetailsMapper implements Function<User, UserDetails> {

    @Override
    public AppUserDetails apply(User u) {
        var authorities = builder().roles(toNames(u.getRoles())).build().getAuthorities();
        return new AppUserDetails(u.getId(), u.getEmail(), u.getUsername(), u.getPassword(), authorities);
    }

    private String[] toNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::name)
                .toArray(String[]::new);
    }

}
