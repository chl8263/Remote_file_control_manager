package com.ewan.rfcm.global.security.token;

import com.ewan.rfcm.domain.account.model.dto.LoginDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LoginPreAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public LoginPreAuthenticationToken(String userId, String password){
        super(userId, password);
    }

    public LoginPreAuthenticationToken(LoginDto loginDto){
        this(loginDto.getUserId(), loginDto.getPassword());
    }

    public String getUserId(){
        return (String) super.getPrincipal();
    }

    public String getUserPassword(){
        return (String) super.getCredentials();
    }
}
