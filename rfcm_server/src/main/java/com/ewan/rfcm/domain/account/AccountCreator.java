package com.ewan.rfcm.domain.account;

import com.ewan.rfcm.Application;
import com.ewan.rfcm.domain.account.data.domain.Account;
import com.ewan.rfcm.domain.account.data.domain.AccountRole;
import com.ewan.rfcm.domain.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class AccountCreator implements ApplicationRunner {

    private AccountService accountService;

    @Autowired
    public AccountCreator (AccountService accountService){
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        accountService.createAccount(new Account("Admin", "Admin", AccountRole.ADMIN));
    }
}
