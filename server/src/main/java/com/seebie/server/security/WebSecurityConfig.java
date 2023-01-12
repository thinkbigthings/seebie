package com.seebie.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;


@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") HttpSecurity http) throws Exception {

        var paths = List.of("/", "/static/**", "/*.png", "/favicon.ico", "/manifest.json", "/actuator/**");
        var openEndpoints = paths.stream()
                .map(AntPathRequestMatcher::new)
                .toList().toArray(new RequestMatcher[paths.size()]);

        var addSessionSaving = new ObjectPostProcessor<BasicAuthenticationFilter>() {
            public BasicAuthenticationFilter postProcess(BasicAuthenticationFilter filter) {
                filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
                return filter;
            }
        };

        http
            .authorizeHttpRequests( customizer -> customizer
                    .requestMatchers(openEndpoints).permitAll()
                    .anyRequest().authenticated() )
            .httpBasic(basic -> basic.withObjectPostProcessor(addSessionSaving))
            .csrf()
                .disable()
            .exceptionHandling()
                .accessDeniedHandler((req, resp, e) -> e.printStackTrace() )
                .and()
            .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true);

        return http.build();
    }
}
