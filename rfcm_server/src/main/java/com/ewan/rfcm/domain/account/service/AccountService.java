package com.ewan.rfcm.domain.account.service;

import com.ewan.rfcm.domain.account.dao.AccountRepository;
import com.ewan.rfcm.domain.account.data.domain.Account;
import com.ewan.rfcm.global.security.AccountContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;

public class AccountService implements UserDetailsService {

    private AccountRepository accountRepository;

    /**
     * Inject dependence object as constructor for forcing dependency object with this class.
    * */
    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserId(username).orElseThrow(() -> new NoSuchElementException("Cannot find account with this id."));
        AccountContext accountContext = AccountContext.fromAccountModel(account);
        return accountContext;
    }
}
