package com.seebie.server.test;

import com.seebie.server.security.AppUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser mockUser) {
        AppUserDetails customPrincipal = new AppUserDetails(
            1L, // Replace with mock user ID
                mockUser.username() + "@example.com", // Email derived from username for testing
            mockUser.legacyUsername(), // legacyUsername
            "password", // Placeholder password
            List.of(new SimpleGrantedAuthority("ROLE_" + mockUser.roles()[0]))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
            customPrincipal,
            "password",
            customPrincipal.getAuthorities()
        ));
        return context;
    }
}