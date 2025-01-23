package com.seebie.server.test;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomSecurityContextFactory.class)
public @interface WithCustomMockUser {
    String userPublicId() default "someuser";
    String username() default "someuser@example.com";
    String[] roles() default {"USER"};
}