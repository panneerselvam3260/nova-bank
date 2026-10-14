package com.novabank.account;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUsernameOrderByIdAsc(String username);
    Optional<Account> findByAccountNumber(String accountNumber);
}