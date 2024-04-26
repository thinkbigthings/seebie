package com.seebie.server.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
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

    public static final String API_LOGIN = "/api/login";
    private static Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    public static final String SESSION_COOKIE = "SESSION";
    public static final String REMEMBER_ME_COOKIE = "remember-me";
    public static final String COOKIE = "Cookie";

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
                    .requestMatchers(openEndpoints).permitAll()
                    .anyRequest().authenticated())
            .httpBasic(basic -> basic.withObjectPostProcessor(new BasicAuthPostProcessor()))
            .exceptionHandling(exceptions -> exceptions
                    .defaultAuthenticationEntryPointFor(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                            new AntPathRequestMatcher(API_LOGIN)
                    )
                    .authenticationEntryPoint(
                            (req, resp, authException) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                    ))
            .requestCache(cache -> cache.requestCache(new NullRequestCache()))
            .sessionManagement(session -> session
                    .invalidSessionStrategy((request, response) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Session expired or invalid");
                        LOG.info(STR."Session expired or invalid. Request cookie: \{request.getHeader(COOKIE)}");
                    })
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true))
            .csrf((csrf) -> csrf.disable())
            .logout(config -> config
                    .logoutUrl("/api/logout")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies(SESSION_COOKIE, REMEMBER_ME_COOKIE)
            )
            .rememberMe(config -> config
                    .rememberMeServices(rememberMeServices));

        return http.build();
    }

}
