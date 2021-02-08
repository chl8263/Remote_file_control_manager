package com.ewan.rfcm.global.security.provider;

import com.ewan.rfcm.domain.account.data.domain.Account;
import com.ewan.rfcm.domain.account.service.AccountService;
import com.ewan.rfcm.global.security.AccountContext;
import com.ewan.rfcm.global.security.token.PreAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.NoSuchElementException;

public class FormLoginAuthenticationProvider implements AuthenticationProvider {

    private AccountService accountService;

    @Autowired
    public FormLoginAuthenticationProvider(AccountService accountService){
        this.accountService = accountService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        PreAuthenticationToken token = (PreAuthenticationToken) authentication;

        String username = token.getUsername();
        String password = token.getUserPassword();

        Account account = accountService.findByUserId(username).orElseThrow(() -> new NoSuchElementException("Cannot find account with this id"));

        if(isCorrectPassword(password, account)){

        }

        return null;
    }


    /**
    * Define class type for support this provider.
    * The class that returned from this method will be filtered to authenticate method on this class.
    * */
    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean isCorrectPassword(String password, Account account){
        return false;
    }
}
