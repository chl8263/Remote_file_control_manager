package com.ewan.rfcm.global.security.token;

import com.ewan.rfcm.global.security.AccountContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Wongyun Choi
 */
public class PostAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private PostAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static PostAuthenticationToken getTokenFromAccountContext(AccountContext context){
        return new PostAuthenticationToken(context, context.getPassword(), context.getAuthorities());
    }
}
