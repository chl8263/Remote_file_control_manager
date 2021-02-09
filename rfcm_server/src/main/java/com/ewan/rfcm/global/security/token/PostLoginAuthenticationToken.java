package com.ewan.rfcm.global.security.token;

import com.ewan.rfcm.global.security.AccountContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Wongyun Choi
 */
public class PostLoginAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private PostLoginAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static PostLoginAuthenticationToken getTokenFromAccountContext(AccountContext context){
        return new PostLoginAuthenticationToken(context, context.getPassword(), context.getAuthorities());
    }
}
