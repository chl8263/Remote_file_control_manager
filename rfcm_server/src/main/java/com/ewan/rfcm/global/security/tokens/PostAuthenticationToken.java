package com.ewan.rfcm.global.security.tokens;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Wongyun Choi
 */
public class PostAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public PostAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
