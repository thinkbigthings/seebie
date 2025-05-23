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
        return new AppUserDetails(u.getEmail(), u.getPublicId(), u.getPassword(), createAuthorities(u));
    }

    private Collection<? extends GrantedAuthority> createAuthorities(User u) {
        return toAuthorities(toNames(u.getRoles()));
    }

    private String[] toNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::name)
                .toArray(String[]::new);
    }

    public static Collection<? extends GrantedAuthority> toAuthorities(String[] roles) {
        // leverage Spring Security's method so we get updates if anything changes
        return builder().username(" ").password("").roles(roles).build().getAuthorities();
    }

}
