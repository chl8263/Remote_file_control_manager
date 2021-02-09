package com.ewan.rfcm.global.security.handler;

import com.ewan.rfcm.Application;
import com.ewan.rfcm.global.security.AccountContext;
import com.ewan.rfcm.global.security.JwtFactory;
import com.ewan.rfcm.global.security.dto.TokenDto;
import com.ewan.rfcm.global.security.token.LoginPostAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private JwtFactory jwtFactory;
    private ObjectMapper objectMapper;


    @Autowired
    public LoginAuthenticationSuccessHandler(
            JwtFactory jwtFactory
            , ObjectMapper objectMapper
    ){
        this.jwtFactory = jwtFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LoginPostAuthenticationToken token = (LoginPostAuthenticationToken) authentication;
        AccountContext accountContext = (AccountContext) token.getPrincipal();
        String tokenString = jwtFactory.generateToken(accountContext);

        ServletContext application = request.getServletContext();

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String userId = accountContext.getUsername();
        String storedSession = (String) session.getAttribute(userId);

        session.setAttribute(userId, sessionId);

        String a = (String) session.getAttribute(userId);

//        if(storedSession == null || storedSession.isEmpty() || storedSession.isBlank() || !sessionId.equals(storedSession)){
//            session.invalidate();
//            if(storedSession != null) session.removeAttribute(sessionId);
//            throw new SessionAuthenticationException("This session isn't valid session");
//        }

        processResponse(response, writeDto(tokenString));
    }

    private TokenDto writeDto(String token){
        return new TokenDto(token);
    }

    private void processResponse(HttpServletResponse response, TokenDto dto) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(objectMapper.writeValueAsString(dto));
    }
}
