
package com.soumya.banking.service;

import com.soumya.banking.account.Account;
import com.soumya.banking.account.AccountRepository;
import com.soumya.banking.api.CreateAccountRequest;
import com.soumya.banking.api.UpdateAccountRequest;
import com.soumya.banking.transaction.BankTransaction;
import com.soumya.banking.transaction.TransactionRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BankingService {

    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public BankingService(
            AccountRepository accounts,
            TransactionRepository transactions,
            JdbcTemplate jdbcTemplate) {
        this.accounts = accounts;
        this.transactions = transactions;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        if (accounts.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        String accountNumber = "BA" + System.currentTimeMillis()
                + ThreadLocalRandom.current().nextInt(100, 1000);

        Account account = new Account(
                request.accountHolder().trim(),
                request.email().trim().toLowerCase(),
                accountNumber
        );

        return accounts.save(account);
    }

    public List<Account> allAccounts() {
        return accounts.findAll();
    }

    public Account getAccount(Long id) {
        return accounts.findById(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"
                )
        );
    }

    @Transactional
    public Account updateAccount(Long id, UpdateAccountRequest request) {
        Account account = getAccount(id);

        accounts.findByEmailIgnoreCase(request.email()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email is already used by another account"
                );
            }
        });

        account.setAccountHolder(request.accountHolder().trim());
        account.setEmail(request.email().trim().toLowerCase());

        return accounts.save(account);
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount) {
        validateAmount(amount);

        Account account = getAccount(id);

        callProcedure("deposit_money", id, amount);

        transactions.save(
                new BankTransaction(id, "DEPOSIT", amount, "Cash deposit")
        );

        // Refresh the entity because the PL/SQL procedure changed the balance.
        entityManager.refresh(account);

        return account;
    }

    @Transactional
    public Account withdraw(Long id, BigDecimal amount) {
        validateAmount(amount);

        Account account = getAccount(id);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient funds"
            );
        }

        callProcedure("withdraw_money", id, amount);

        transactions.save(
                new BankTransaction(id, "WITHDRAWAL", amount, "Cash withdrawal")
        );

        // Refresh the entity because the PL/SQL procedure changed the balance.
        entityManager.refresh(account);

        return account;
    }

    public List<BankTransaction> history(Long id) {
        getAccount(id);
        return transactions.findByAccountIdOrderByCreatedAtDesc(id);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount must be greater than zero"
            );
        }

        if (amount.scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount cannot have more than two decimal places"
            );
        }
    }

    private void callProcedure(
            String procedure,
            Long accountId,
            BigDecimal amount) {

        String sql = "{call " + procedure + "(?, ?)}";

        jdbcTemplate.execute(connection -> {
            CallableStatement statement = connection.prepareCall(sql);
            statement.setLong(1, accountId);
            statement.setBigDecimal(2, amount);
            return statement;
        }, (CallableStatement statement) -> {
            statement.execute();
            return null;
        });
    }
}
