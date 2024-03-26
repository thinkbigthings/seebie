package com.seebie.server.security;

import com.seebie.server.entity.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private static Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    public static final String SESSION_COOKIE = "SESSION";
    public static final String REMEMBER_ME_COOKIE = "remember-me";


    // TODO This can be replaced with a simpler API call as of Spring Security 6.1.0
    // See https://github.com/spring-projects/spring-security/issues/12031
    private static class BasicAuthPostProcessor implements ObjectPostProcessor<BasicAuthenticationFilter> {
        @Override
        public <O extends BasicAuthenticationFilter> O postProcess(O filter) {
            filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
            return filter;
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RememberMeServices rememberMeServices) throws Exception {

        var paths = List.of("/", "/index.html", "/favicon.ico", "/manifest.json", "/assets/**");
        var openEndpoints = paths.stream().map(AntPathRequestMatcher::new).toList().toArray(new RequestMatcher[paths.size()]);

        http
            .requiresChannel(channel -> channel.anyRequest().requiresSecure())
            .authorizeHttpRequests(customizer -> customizer
//                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.name()) // restrict actuator to admin
                    .requestMatchers(openEndpoints).permitAll()
                    .anyRequest().authenticated())
            .httpBasic(basic -> basic.withObjectPostProcessor(new BasicAuthPostProcessor()))
            .requestCache(cache -> cache.requestCache(new NullRequestCache()))
            .sessionManagement(session -> session
                    .invalidSessionStrategy((request, response) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Session expired or invalid");
                        LOG.info("Session expired or invalid. Request cookies: " + request.getHeader("Cookie"));
                    })
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true))
            .csrf((csrf) -> csrf.disable())
            .logout(config -> config
                    .addLogoutHandler((req, resp, auth) -> LOG.info("Logged out auth: " + auth))
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies(SESSION_COOKIE, REMEMBER_ME_COOKIE)
            )
            .rememberMe(config -> config
                    .rememberMeServices(rememberMeServices));

        return http.build();
    }

}
