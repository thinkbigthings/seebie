package com.seebie.server.test;

import com.seebie.server.security.AppUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser user) {

        var publicId = user.userPublicId();
        var loginId = user.username();
        var roles = Arrays.stream(user.roles())
                .map(r -> "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();

        var customPrincipal = new AppUserDetails(loginId, publicId, "password", roles);

        var authToken = new UsernamePasswordAuthenticationToken(customPrincipal,"password", roles);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        return context;
    }
}