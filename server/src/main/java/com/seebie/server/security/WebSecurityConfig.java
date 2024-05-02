package com.seebie.server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private static Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    public static final String API_LOGIN = "/api/login";
    public static final String API_LOGOUT = "/api/logout";

    public static final String SESSION_COOKIE = "SESSION";
    public static final String REMEMBER_ME_COOKIE = "remember-me";
    public static final String COOKIE = "Cookie";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RememberMeServices rememberMeServices) throws Exception {

        var paths = List.of("/", "/index.html", "/favicon.ico", "/manifest.json", "/assets/**");
        var openEndpoints = paths.stream().map(AntPathRequestMatcher::new).toList().toArray(new RequestMatcher[paths.size()]);

        http
            .requiresChannel(channel -> channel
                    .anyRequest().requiresSecure())
            .authorizeHttpRequests(customizer -> customizer
                    .requestMatchers(openEndpoints).permitAll()
                    .anyRequest().authenticated())
            .exceptionHandling(customizer -> customizer
                .authenticationEntryPoint(this::unauthenticatedAccess))
            .httpBasic(basic -> basic
                    .securityContextRepository(new HttpSessionSecurityContextRepository()))
            .requestCache(cache -> cache
                    .requestCache(new NullRequestCache()))
            .sessionManagement(session -> session
                    .invalidSessionStrategy(this::invalidSession)
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true))
            .csrf((csrf) -> csrf
                    .disable())
            .logout(config -> config
                    .logoutUrl(API_LOGOUT)
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies(SESSION_COOKIE, REMEMBER_ME_COOKIE))
            .rememberMe(config -> config
                    .rememberMeServices(rememberMeServices));

        return http.build();
    }

     // Alternatively could implement AuthenticationEntryPoint and wrap BasicAuthenticationEntryPoint
     // to delegate to it while adding custom logging.
     private void unauthenticatedAccess(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized access attempt");
        response.setHeader("WWW-Authenticate", "Basic realm=\"Access to secured area requires authentication\"");
        LOG.warn(STR."\{authException} at \{request.getRequestURI()}");
    }

    private void invalidSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Session expired or invalid");
        LOG.warn(STR."Session expired or invalid. Request cookie: \{request.getHeader(COOKIE)} accessing \{request.getRequestURI()}");
    }

}
