package com.ewan.rfcm.global.security.token;

import com.ewan.rfcm.domain.account.data.domain.AccountRole;
import com.ewan.rfcm.global.security.AccountContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class JwtPostProcessingToken extends UsernamePasswordAuthenticationToken {


    public JwtPostProcessingToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public JwtPostProcessingToken(String username, AccountRole accountRole){
        super(username, "***", parseAuthorities(accountRole));
    }

    public static JwtPostProcessingToken getTokenFromAccountContext(AccountContext context){
        return new JwtPostProcessingToken(context, context.getPassword(), context.getAuthorities());
    }

    private static Collection<? extends GrantedAuthority> parseAuthorities(AccountRole accountRole){
        return Arrays.asList(accountRole).stream().map(x -> new SimpleGrantedAuthority(accountRole.getRoleName())).collect(Collectors.toList());
    }

    public String getUserId(){
        AccountContext accountContext = (AccountContext) super.getPrincipal();
        return accountContext.getUsername ();
    }

    public String getPassword(){
        return (String)super.getCredentials();
    }
}
