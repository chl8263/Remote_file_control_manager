package com.ewan.rfcm.domain.account.service;

import com.ewan.rfcm.domain.account.model.domain.Account;
import com.ewan.rfcm.domain.account.repository.AccountRepository;
import com.ewan.rfcm.global.security.AccountContext;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserId(username).orElseThrow(() -> new NoSuchElementException("Cannot find account with this id."));
        AccountContext accountContext = AccountContext.fromAccountModel(account);
        return accountContext;
    }

    public Optional<Account> findByUserId(String userId){
        return accountRepository.findByUserId(userId);
    }

    public Account createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }
}
