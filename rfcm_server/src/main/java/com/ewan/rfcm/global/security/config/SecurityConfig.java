package com.ewan.rfcm.global.security.config;

import com.ewan.rfcm.domain.account.service.AccountService;
import com.ewan.rfcm.global.security.filter.LoginAuthenticationFilter;
import com.ewan.rfcm.global.security.handler.LoginAuthenticationFailureHandler;
import com.ewan.rfcm.global.security.handler.LoginAuthenticationSuccessHandler;
import com.ewan.rfcm.global.security.provider.LoginAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity // Active spring security
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AccountService accountService;
    private PasswordEncoder passwordEncoder;
    private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
    private LoginAuthenticationProvider loginAuthenticationProvider;

    @Autowired
    public SecurityConfig(
            AccountService accountService
            , PasswordEncoder passwordEncoder
            , LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler
            , LoginAuthenticationFailureHandler loginAuthenticationFailureHandler
            , LoginAuthenticationProvider loginAuthenticationProvider
    ){
        this.accountService = accountService;
        this.loginAuthenticationSuccessHandler = loginAuthenticationSuccessHandler;
        this.loginAuthenticationFailureHandler = loginAuthenticationFailureHandler;
        this.loginAuthenticationProvider = loginAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception { return super.authenticationManagerBean(); }

    protected LoginAuthenticationFilter loginAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter("/authLogin", loginAuthenticationSuccessHandler, loginAuthenticationFailureHandler);
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(this.loginAuthenticationProvider)
                .userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }
}
