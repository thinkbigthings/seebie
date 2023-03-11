package com.seebie.server.security;

import com.seebie.server.controller.UserController;
import com.seebie.server.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private static Logger LOG = LoggerFactory.getLogger(UserController.class);

    private static class BasicAuthPostProcessor implements ObjectPostProcessor<BasicAuthenticationFilter> {
        @Override
        public <O extends BasicAuthenticationFilter> O postProcess(O filter) {
            filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
            return filter;
        }
    }

    @Bean
    public SecurityFilterChain filterChain(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") HttpSecurity http) throws Exception {

        var paths = List.of("/", "/index.html", "/static/**", "/*.png", "/favicon.ico", "/manifest.json");
        var openEndpoints = paths.stream()
                .map(AntPathRequestMatcher::new)
                .toList().toArray(new RequestMatcher[paths.size()]);

        http
            .requiresChannel((channel) -> channel.anyRequest().requiresSecure())
            .authorizeHttpRequests( customizer -> customizer
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.name())
                    .requestMatchers(openEndpoints).permitAll()
                    .anyRequest().authenticated() )
            .httpBasic(basic -> basic.withObjectPostProcessor(new BasicAuthPostProcessor()))
            .csrf()
                .disable()
            .logout()
                .addLogoutHandler((HttpServletRequest req, HttpServletResponse resp, Authentication auth) ->
                        // login/logout logging should use the same object, so we can link login events with logout events
                        LOG.info("Logged out auth: " + auth))
                .invalidateHttpSession(true)
                .clearAuthentication(true);

        return http.build();
    }

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
