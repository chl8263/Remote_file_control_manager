package com.ewan.rfcm.global.security.handler;

import com.ewan.rfcm.global.security.AccountContext;
import com.ewan.rfcm.global.security.JwtFactory;
import com.ewan.rfcm.global.security.dto.ErrorDto;
import com.ewan.rfcm.global.security.token.PostAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginAuthenticationFailureHandler.class);

    private ObjectMapper objectMapper;

    @Autowired
    public LoginAuthenticationFailureHandler(
            ObjectMapper objectMapper
    ){
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        //response.getWriter(objectMapper.writeValueAsString(new ErrorDto(Integer.toString(HttpStatus.BAD_REQUEST.value()), "Cannot create JWT token")));
    }
}
