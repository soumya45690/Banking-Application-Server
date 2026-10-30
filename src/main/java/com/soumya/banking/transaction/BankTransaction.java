package com.soumya.banking.transaction;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BANK_TRANSACTIONS")
public class BankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    private Long id;

    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name = "TRANSACTION_TYPE", nullable = false, length = 20)
    private String type;

    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "DESCRIPTION", length = 250)
    private String description;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected BankTransaction() {}
    public BankTransaction(Long accountId, String type, BigDecimal amount, String description) {
        this.accountId = accountId; this.type = type; this.amount = amount;
        this.description = description; this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
