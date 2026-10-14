package com.novabank.auth;

import com.novabank.security.JwtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = "com.novabank.auth")
@Import(JwtService.class)
public class AuthServiceApplication {
    public static void main(String[] args) { SpringApplication.run(AuthServiceApplication.class, args); }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    CommandLineRunner seedUser(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.findByUsername("demo@novabank.com").isEmpty()) {
                repository.save(new BankUser(null, "demo@novabank.com", encoder.encode("password"), "CUSTOMER"));
            }
        };
    }
}
