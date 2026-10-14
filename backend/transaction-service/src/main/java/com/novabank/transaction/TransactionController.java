package com.novabank.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionRepository repository;
    private final RestClient accountClient;

    public TransactionController(TransactionRepository repository, RestClient accountClient) { this.repository=repository; this.accountClient=accountClient; }

    @GetMapping
    public List<TransactionResponse> history(Authentication auth) {
        return repository.findTop20ByUsernameOrderByCreatedAtDesc(auth.getName()).stream().map(TransactionResponse::from).toList();
    }

    @PostMapping("/transfer")
    @Transactional
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request, Authentication auth) {
        if (request.fromAccount().equals(request.toAccount())) throw new IllegalArgumentException("Source and destination accounts must differ");
        String bearer = "Bearer " + currentTokenRequired(auth);
        accountClient.post().uri("/api/accounts/{account}/debit", request.fromAccount())
                .header("Authorization", bearer)
                .body(new AmountRequest(request.amount()))
                .retrieve().toBodilessEntity();
        accountClient.post().uri("/api/accounts/{account}/credit", request.toAccount())
                .header("Authorization", bearer)
                .body(new AmountRequest(request.amount()))
                .retrieve().toBodilessEntity();
        return TransactionResponse.from(repository.save(new BankTransaction(auth.getName(), request.fromAccount(), request.toAccount(), request.amount(), "SUCCESS")));
    }

    @GetMapping("/health")
    public String health() { return "transaction-service is UP"; }

    private String currentTokenRequired(Authentication auth) {
        Object credentials = auth.getDetails();
        if (credentials instanceof String s && !s.isBlank()) return s;
        throw new IllegalArgumentException("Missing authenticated token");
    }

    public record TransferRequest(@NotBlank String fromAccount, @NotBlank String toAccount, @NotNull @DecimalMin("0.01") BigDecimal amount) {}
    public record AmountRequest(BigDecimal amount) {}
    public record TransactionResponse(Long id, String fromAccount, String toAccount, BigDecimal amount, String status, java.time.OffsetDateTime createdAt) {
        static TransactionResponse from(BankTransaction t) { return new TransactionResponse(t.getId(), t.getFromAccount(), t.getToAccount(), t.getAmount(), t.getStatus(), t.getCreatedAt()); }
    }
}
