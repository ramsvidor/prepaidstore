package com.ramsesvidor.prepaidstore.infrastructure;

import com.ramsesvidor.prepaidstore.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.toAccountId = :accountId AND t.currency = :currency")
    BigDecimal sumCredits(@Param("accountId") UUID accountId, @Param("currency") String currency);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccountId = :accountId AND t.currency = :currency")
    BigDecimal sumDebits(@Param("accountId") UUID accountId, @Param("currency") String currency);

}