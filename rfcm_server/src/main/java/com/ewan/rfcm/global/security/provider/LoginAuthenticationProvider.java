package com.ewan.rfcm.global.security.provider;

import com.ewan.rfcm.domain.account.data.domain.Account;
import com.ewan.rfcm.domain.account.service.AccountService;
import com.ewan.rfcm.global.security.AccountContext;
import com.ewan.rfcm.global.security.token.LoginPostAuthenticationToken;
import com.ewan.rfcm.global.security.token.LoginPreAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private AccountService accountService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginAuthenticationProvider(
            AccountService accountService
            , PasswordEncoder passwordEncoder
    ){
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginPreAuthenticationToken token = (LoginPreAuthenticationToken) authentication;

        String userId = token.getUserId();
        String password = token.getUserPassword();
        Account account = accountService.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Cannot find account with this id"));

        if (isCorrectPassword(password, account)) {
            return LoginPostAuthenticationToken.getTokenFromAccountContext(AccountContext.fromAccountModel(account));
        }
        throw new NoSuchElementException("Not match with this information");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LoginPreAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean isCorrectPassword(String password, Account account){
        return passwordEncoder.matches(password, account.getPassword());
    }
}
