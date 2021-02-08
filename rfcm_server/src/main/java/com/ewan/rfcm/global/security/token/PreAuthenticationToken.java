package com.ewan.rfcm.global.security.token;

import com.ewan.rfcm.domain.account.data.dto.LoginDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class PreAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public PreAuthenticationToken(String userName, String password){
        super(userName, password);
    }

    public PreAuthenticationToken(LoginDto loginDto){
        this(loginDto.getUserId(), loginDto.getPassword());
    }

    public String getUsername(){
        return (String) super.getPrincipal();
    }

    public String getUserPassword(){
        return (String) super.getCredentials();
    }
}
