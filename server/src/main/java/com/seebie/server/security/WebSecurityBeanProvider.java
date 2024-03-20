package com.seebie.server.security;

import com.seebie.server.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

import static com.seebie.server.security.WebSecurityConfig.REMEMBER_ME_COOKIE;

@Configuration
public class WebSecurityBeanProvider {

    private AppProperties.Security.RememberMe rememberMeConfig;

    public WebSecurityBeanProvider(AppProperties app) {
        rememberMeConfig = app.security().rememberMe();
    }

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public RememberMeServices rememberMeServices(PersistentTokenRepository persistentTokenRepo, UserDetailsService userDetailsService) {

        var rememberMe = new PersistentTokenBasedRememberMeServices(rememberMeConfig.key(), userDetailsService, persistentTokenRepo);

        rememberMe.setParameter(REMEMBER_ME_COOKIE);
        rememberMe.setTokenValiditySeconds(rememberMeConfig.tokenValiditySeconds());
        rememberMe.setCookieName(REMEMBER_ME_COOKIE);
        rememberMe.setUseSecureCookie(true);

        return rememberMe;
    }
}
