package com.ewan.rfcm.global.security.provider;

import com.ewan.rfcm.global.security.AccountContext;
import com.ewan.rfcm.global.security.JwtDecoder;
import com.ewan.rfcm.global.security.token.JwtPostProcessingToken;
import com.ewan.rfcm.global.security.token.JwtPreProcessingToken;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        AccountContext accountContext = jwtDecoder.decodeJwt(token);
        return JwtPostProcessingToken.getTokenFromAccountContext(accountContext);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
}
