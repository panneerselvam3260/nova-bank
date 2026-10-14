package com.novabank.account;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts", indexes = @Index(name = "idx_account_user", columnList = "username"))
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String accountType;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false)
    private String status;

    protected Account() {}
    public Account(Long id, String accountNumber, String username, String accountType, String currency, BigDecimal balance, String status) {
        this.id=id; this.accountNumber=accountNumber; this.username=username; this.accountType=accountType; this.currency=currency; this.balance=balance; this.status=status;
    }
    public Long getId(){return id;} public String getAccountNumber(){return accountNumber;} public String getUsername(){return username;}
    public String getAccountType(){return accountType;} public String getCurrency(){return currency;} public BigDecimal getBalance(){return balance;} public String getStatus(){return status;}
    public void setBalance(BigDecimal balance){this.balance=balance;}
}
