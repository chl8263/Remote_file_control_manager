package com.ewan.rfcm.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {
    @Bean
    public PasswordEncoder getPasswordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }
}
