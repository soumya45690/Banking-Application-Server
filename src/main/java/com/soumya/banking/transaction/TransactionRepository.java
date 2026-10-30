package com.soumya.banking.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}
