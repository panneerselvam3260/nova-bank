package com.novabank.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountRepository repository;
    public AccountController(AccountRepository repository) { this.repository = repository; }

    @GetMapping
    public List<AccountResponse> myAccounts(Authentication auth) {
        return repository.findByUsernameOrderByIdAsc(auth.getName()).stream().map(AccountResponse::from).toList();
    }

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request, Authentication auth) {
        String number = "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 9).toUpperCase();
        Account account = repository.save(new Account(null, number, auth.getName(), request.accountType(), "INR", BigDecimal.ZERO, "ACTIVE"));
        return AccountResponse.from(account);
    }

    @PostMapping("/{accountNumber}/debit")
    @Transactional
    public AccountResponse debit(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request, Authentication auth) {
        Account account = ownAccount(accountNumber, auth);
        if (account.getBalance().compareTo(request.amount()) < 0) throw new IllegalArgumentException("Insufficient balance");
        account.setBalance(account.getBalance().subtract(request.amount()));
        return AccountResponse.from(repository.save(account));
    }

    @PostMapping("/{accountNumber}/credit")
    @Transactional
    public AccountResponse credit(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request, Authentication auth) {
        Account account = ownAccount(accountNumber, auth);
        account.setBalance(account.getBalance().add(request.amount()));
        return AccountResponse.from(repository.save(account));
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse find(@PathVariable String accountNumber, Authentication auth) { return AccountResponse.from(ownAccount(accountNumber, auth)); }

    @GetMapping("/health")
    public String health() { return "account-service is UP"; }

    private Account ownAccount(String number, Authentication auth) {
        Account a = repository.findByAccountNumber(number).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!a.getUsername().equals(auth.getName())) throw new IllegalArgumentException("Account does not belong to current user");
        return a;
    }

    public record CreateAccountRequest(@NotBlank String accountType) {}
    public record AmountRequest(@NotNull @DecimalMin("0.01") BigDecimal amount) {}
    public record AccountResponse(String accountNumber, String accountType, String currency, BigDecimal balance, String status) {
        static AccountResponse from(Account a) { return new AccountResponse(a.getAccountNumber(), a.getAccountType(), a.getCurrency(), a.getBalance(), a.getStatus()); }
    }
}
