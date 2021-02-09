package com.ewan.rfcm.global.security.token;

import com.ewan.rfcm.global.security.AccountContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Wongyun Choi
 */
public class LoginPostAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private LoginPostAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static LoginPostAuthenticationToken getTokenFromAccountContext(AccountContext context){
        return new LoginPostAuthenticationToken(context, context.getPassword(), context.getAuthorities());
    }
}
