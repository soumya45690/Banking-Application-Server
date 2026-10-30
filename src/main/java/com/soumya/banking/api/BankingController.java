package com.soumya.banking.api;

import com.soumya.banking.account.Account;
import com.soumya.banking.service.BankingService;
import com.soumya.banking.transaction.BankTransaction;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BankingController {
    private final BankingService service;
    public BankingController(BankingService service) { this.service = service; }

    @PostMapping("/accounts")
    public Account createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return service.createAccount(request);
    }

    @GetMapping("/accounts")
    public List<Account> allAccounts() { return service.allAccounts(); }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id) { return service.getAccount(id); }

    @PutMapping("/accounts/{id}")
    public Account updateAccount(@PathVariable Long id, @Valid @RequestBody UpdateAccountRequest request) {
        return service.updateAccount(id, request);
    }

    @GetMapping("/accounts/{id}/balance")
    public Map<String, Object> balance(@PathVariable Long id) {
        Account account = service.getAccount(id);
        return Map.of("accountId", id, "accountNumber", account.getAccountNumber(), "balance", account.getBalance());
    }

    @PostMapping("/accounts/{id}/deposit")
    public Account deposit(@PathVariable Long id, @Valid @RequestBody AmountRequest request) {
        return service.deposit(id, request.amount());
    }

    @PostMapping("/accounts/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @Valid @RequestBody AmountRequest request) {
        return service.withdraw(id, request.amount());
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<BankTransaction> history(@PathVariable Long id) { return service.history(id); }
}
