package com.novabank.transaction;

import com.novabank.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@SpringBootApplication(scanBasePackages = "com.novabank.transaction")
@Import(SecurityConfig.class)
public class TransactionServiceApplication {
    public static void main(String[] args) { SpringApplication.run(TransactionServiceApplication.class, args); }

    @Bean
    RestClient accountClient(org.springframework.core.env.Environment env) {
        return RestClient.builder().baseUrl(env.getProperty("bank.services.account-url", "http://localhost:8082")).build();
    }
}
