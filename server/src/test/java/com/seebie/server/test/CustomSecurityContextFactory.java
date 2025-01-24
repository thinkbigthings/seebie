package com.seebie.server.test;

import com.seebie.server.security.AppUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


import static com.seebie.server.mapper.entitytodto.UserDetailsMapper.toAuthorities;

public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser user) {

        var publicId = user.publicId();
        var loginId = user.username();
        var roles = toAuthorities(user.roles());
        var password = "password";

        var customPrincipal = new AppUserDetails(loginId, publicId, password, roles);

        var authToken = new UsernamePasswordAuthenticationToken(customPrincipal,password, roles);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);

        return context;
    }
}