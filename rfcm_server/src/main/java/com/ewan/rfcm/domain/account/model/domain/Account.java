package com.ewan.rfcm.domain.account.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ACCOUNT_USERID", length = 70, nullable = false)
    private String userId;

    @Column(name = "ACCOUNT_PASSWORD", length = 200, nullable = false)
    private String password;

    @Column(name = "ACCOUNT_ROLE", length = 20, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountRole accountRole = AccountRole.USER;

    public Account(String userId, String password, AccountRole accountRole) {
        this.userId =  userId;
        this.password = password;
        this.accountRole = accountRole;
    }
}
