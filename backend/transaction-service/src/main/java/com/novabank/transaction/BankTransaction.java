package com.novabank.transaction;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions", indexes = @Index(name = "idx_tx_user_time", columnList = "username,createdAt"))
public class BankTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String fromAccount;
    @Column(nullable = false)
    private String toAccount;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected BankTransaction() {}
    public BankTransaction(String username, String fromAccount, String toAccount, BigDecimal amount, String status) {
        this.username=username; this.fromAccount=fromAccount; this.toAccount=toAccount; this.amount=amount; this.status=status; this.createdAt=OffsetDateTime.now();
    }
    public Long getId(){return id;} public String getFromAccount(){return fromAccount;} public String getToAccount(){return toAccount;} public BigDecimal getAmount(){return amount;} public String getStatus(){return status;} public OffsetDateTime getCreatedAt(){return createdAt;}
}
