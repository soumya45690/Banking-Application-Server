package com.soumya.banking.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BANK_ACCOUNTS")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long id;

    @Column(name = "ACCOUNT_HOLDER", nullable = false, length = 100)
    private String accountHolder;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "ACCOUNT_NUMBER", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "BALANCE", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Account() {}
    public Account(String accountHolder, String email, String accountNumber) {
        this.accountHolder = accountHolder;
        this.email = email;
        this.accountNumber = accountNumber;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
