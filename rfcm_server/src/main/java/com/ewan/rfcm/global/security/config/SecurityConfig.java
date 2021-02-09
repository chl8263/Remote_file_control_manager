package com.ewan.rfcm.global.security.config;

import com.ewan.rfcm.domain.account.service.AccountService;
import com.ewan.rfcm.global.security.FilterSkipMatcher;
import com.ewan.rfcm.global.security.HeaderTokenExtractor;
import com.ewan.rfcm.global.security.filter.JwtAuthenticationFilter;
import com.ewan.rfcm.global.security.filter.LoginAuthenticationFilter;
import com.ewan.rfcm.global.security.handler.JwtAuthenticationFailureHandler;
import com.ewan.rfcm.global.security.handler.JwtAuthenticationSuccessHandler;
import com.ewan.rfcm.global.security.handler.LoginAuthenticationFailureHandler;
import com.ewan.rfcm.global.security.handler.LoginAuthenticationSuccessHandler;
import com.ewan.rfcm.global.security.provider.JwtAuthenticationProvider;
import com.ewan.rfcm.global.security.provider.LoginAuthenticationProvider;
import lombok.val;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // Active spring security
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AccountService accountService;
    private PasswordEncoder passwordEncoder;

    private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
    private LoginAuthenticationProvider loginAuthenticationProvider;

    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    private HeaderTokenExtractor headerTokenExtractor;

    @Autowired
    public SecurityConfig(
            AccountService accountService
            , PasswordEncoder passwordEncoder

            , LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler
            , LoginAuthenticationFailureHandler loginAuthenticationFailureHandler
            , LoginAuthenticationProvider loginAuthenticationProvider

            , JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler
            , JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler
            , JwtAuthenticationProvider jwtAuthenticationProvider

            , HeaderTokenExtractor headerTokenExtractor
    ){
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;

        this.loginAuthenticationSuccessHandler = loginAuthenticationSuccessHandler;
        this.loginAuthenticationFailureHandler = loginAuthenticationFailureHandler;
        this.loginAuthenticationProvider = loginAuthenticationProvider;

        this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
        this.jwtAuthenticationFailureHandler = jwtAuthenticationFailureHandler;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.headerTokenExtractor = headerTokenExtractor;
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception { return super.authenticationManagerBean(); }

    protected LoginAuthenticationFilter loginAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter("/auth", loginAuthenticationSuccessHandler, loginAuthenticationFailureHandler);
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    protected JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        FilterSkipMatcher filterSkipMatcher = new FilterSkipMatcher(Arrays.asList("/login"), "/api/**");
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(filterSkipMatcher, jwtAuthenticationSuccessHandler, jwtAuthenticationFailureHandler, headerTokenExtractor);
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(this.loginAuthenticationProvider)
                .authenticationProvider(this.jwtAuthenticationProvider)
                .userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource());

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .csrf().disable();

        http
                .headers().frameOptions().disable();

        http
                .addFilterBefore(loginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
