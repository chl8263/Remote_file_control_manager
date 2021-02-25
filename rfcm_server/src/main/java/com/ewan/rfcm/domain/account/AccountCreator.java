package com.ewan.rfcm.domain.account;

import com.ewan.rfcm.domain.account.model.domain.Account;
import com.ewan.rfcm.domain.account.model.domain.AccountRole;
import com.ewan.rfcm.domain.account.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AccountCreator implements ApplicationRunner {

    private final AccountService accountService;

    @Override
    public void run(ApplicationArguments args) {
        accountService.createAccount(new Account("admin", "admin", AccountRole.ADMIN));
        accountService.createAccount(new Account("user", "1234", AccountRole.USER));
    }
}
