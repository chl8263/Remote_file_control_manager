package com.ewan.rfcm.global.security.token;

import com.ewan.rfcm.domain.account.data.dto.LoginDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class PreAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public PreAuthenticationToken(String userId, String password){
        super(userId, password);
    }

    public PreAuthenticationToken(LoginDto loginDto){
        this(loginDto.getUserId(), loginDto.getPassword());
    }

    public String getUserId(){
        return (String) super.getPrincipal();
    }

    public String getUserPassword(){
        return (String) super.getCredentials();
    }
}
