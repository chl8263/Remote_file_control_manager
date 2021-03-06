package com.ewan.rfcm.global.security.handler;

import com.ewan.rfcm.global.constant.UserConnection;
import com.ewan.rfcm.global.security.AccountContext;
import com.ewan.rfcm.global.security.JwtFactory;
import com.ewan.rfcm.global.security.dto.TokenDto;
import com.ewan.rfcm.global.security.token.LoginPostAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtFactory jwtFactory;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        LoginPostAuthenticationToken token = (LoginPostAuthenticationToken) authentication;
        AccountContext accountContext = (AccountContext) token.getPrincipal();
        String tokenString = jwtFactory.generateToken(accountContext);

        String userId = accountContext.getUsername();
        String uid = UUID.randomUUID().toString();
        UserConnection.userConnections.put(userId, uid);

        processResponse(response, writeDto(tokenString, uid));
    }

    private TokenDto writeDto(String token, String uid){
        return new TokenDto(token, uid);
    }

    private void processResponse(HttpServletResponse response, TokenDto dto) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(objectMapper.writeValueAsString(dto));
    }
}
