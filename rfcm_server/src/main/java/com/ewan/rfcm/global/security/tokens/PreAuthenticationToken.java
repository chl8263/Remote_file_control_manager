package com.ewan.rfcm.global.security.tokens;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class PreAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public PreAuthenticationToken(String userName, String password){
        super(userName, password);
    }

    public String getUsername(){
        return (String) super.getPrincipal();
    }

    public String getUserPassword(){
        return (String) super.getCredentials();
    }
}
