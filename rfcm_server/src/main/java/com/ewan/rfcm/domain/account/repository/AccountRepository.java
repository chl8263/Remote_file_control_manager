package com.ewan.rfcm.domain.account.repository;

import com.ewan.rfcm.domain.account.data.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Wongyun Choi
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(String userId);
}
