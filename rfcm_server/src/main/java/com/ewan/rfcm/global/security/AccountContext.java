package com.ewan.rfcm.global.security;

import com.ewan.rfcm.domain.account.data.domain.Account;
import com.ewan.rfcm.domain.account.data.domain.AccountRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AccountContext extends User {

    private AccountContext(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public static AccountContext fromAccountModel(Account account){
        return new AccountContext(account.getUserId(), account.getPassword(), parseAuthorities(account.getAccountRole()));
    }

    private static List<SimpleGrantedAuthority> parseAuthorities(AccountRole role){
        return Arrays.asList(role).stream().map(x -> new SimpleGrantedAuthority(x.getRoleName())).collect(Collectors.toList());
    }
}
