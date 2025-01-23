package com.seebie.server.mapper.entitytodto;

import com.seebie.server.security.AppUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;

import static org.springframework.security.core.userdetails.User.builder;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class UserDetailsMapper implements Function<User, UserDetails> {

    @Override
    public AppUserDetails apply(User u) {
        return new AppUserDetails(u.getEmail(), u.getUsername(), u.getPassword(), createAuthorities(u));
    }

    private Collection<? extends GrantedAuthority> createAuthorities(User u) {
        // leverage Spring Security's method so we get updates if anything changes
        return builder().username(" ").password("").roles(toNames(u.getRoles())).build().getAuthorities();
    }

    public static String[] toNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::name)
                .toArray(String[]::new);
    }

}
