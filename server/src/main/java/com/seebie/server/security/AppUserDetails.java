package com.seebie.server.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AppUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String legacyUsername;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public AppUserDetails(Long userId, String email, String legacyUsername, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = userId;
        this.email = email;
        this.legacyUsername = legacyUsername;
        this.password = password;
        this.authorities = Collections.unmodifiableCollection(authorities);
    }

    public String getLegacyUsername() {
        return legacyUsername;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
