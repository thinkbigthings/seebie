package com.seebie.server.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * By extending UserDetails to include a separate identifier
 * we're cleanly separating the authentication identifier (email)
 * from the public identifier (used in url path).
 */
public class AppUserDetails implements UserDetails {

    private String email;
    private UUID publicId;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public AppUserDetails(String email, UUID publicId, String password, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.publicId = publicId;
        this.password = password;
        this.authorities = Collections.unmodifiableCollection(authorities);
    }

    /**
     * This gets referenced in the @PreAuthorize SPeL.
     *
     * @return
     */
    public UUID getPublicId() {
        return publicId;
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
