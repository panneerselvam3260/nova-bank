package com.novabank.account;

import com.novabank.security.SecurityConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.novabank.account")
@Import(SecurityConfig.class)
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAccounts(AccountRepository repository) {
        return args -> {
            if (repository.findByUsernameOrderByIdAsc("demo@novabank.com").isEmpty()) {
                repository.save(new Account(null, "ACC10001", "demo@novabank.com", "PRIMARY SAVINGS", "INR", new java.math.BigDecimal("125000.00"), "ACTIVE"));
                repository.save(new Account(null, "ACC10002", "demo@novabank.com", "SALARY ACCOUNT", "INR", new java.math.BigDecimal("67500.00"), "ACTIVE"));
            }
        };
    }
}