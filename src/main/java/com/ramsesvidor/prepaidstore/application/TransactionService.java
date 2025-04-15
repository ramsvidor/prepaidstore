package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.AccountType;
import com.ramsesvidor.prepaidstore.domain.Transaction;
import com.ramsesvidor.prepaidstore.domain.TransactionType;
import com.ramsesvidor.prepaidstore.infrastructure.TransactionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    public static final String DEFAULT_CURRENCY = "USD";
    private static final UUID SYSTEM_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction purchase(@NotNull Account user,
                                @NotNull Account merchant,
                                @NotNull BigDecimal totalAmount) {
        if (user.getAccountType() != AccountType.USER) {
            throw new IllegalArgumentException("Only USER accounts can purchase.");
        }

        if (merchant.getAccountType() != AccountType.MERCHANT) {
            throw new IllegalArgumentException("Only MERCHANT accounts can sell.");
        }

        return save(user.getId(), merchant.getId(), totalAmount, TransactionType.PURCHASE);
    }

    @Transactional
    public Transaction recharge(Account user, BigDecimal amount) {
        if (user.getAccountType() != AccountType.USER) {
            throw new IllegalArgumentException("Only USER accounts can be recharged.");
        }

        return save(SYSTEM_ACCOUNT_ID, user.getId(), amount, TransactionType.RECHARGE);
    }

    public BigDecimal calculateAccountBalance(String accountId) {
        return calculateAccountBalance(accountId, DEFAULT_CURRENCY);
    }

    public BigDecimal calculateAccountBalance(String accountId, String currency) {
        return sumCredits(accountId, currency).subtract(sumDebits(accountId, currency));
    }

    private Transaction save(UUID from, UUID to, BigDecimal amount, TransactionType trxType) {
        return transactionRepository.save(new Transaction(
                UUID.randomUUID(),
                from,
                to,
                amount,
                DEFAULT_CURRENCY,
                LocalDateTime.now(),
                trxType
        ));
    }

    private BigDecimal sumCredits(String accountId, String currency) {
        return transactionRepository.sumCredits(UUID.fromString(accountId), currency);
    }

    private BigDecimal sumDebits(String accountId, String currency) {
        return transactionRepository.sumDebits(UUID.fromString(accountId), currency);
    }

}
