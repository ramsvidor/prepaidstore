package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.AccountType;
import com.ramsesvidor.prepaidstore.infrastructure.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final UUID userId = UUID.randomUUID();
    private final UUID merchantId = UUID.randomUUID();

    private Account user;
    private Account merchant;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new Account(userId, "Test User", AccountType.USER);
        merchant = new Account(merchantId, "Test Merchant", AccountType.MERCHANT);
    }

    @Test
    void purchaseShouldSaveTransaction() {
        transactionService.purchase(user, merchant, BigDecimal.TEN);

        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void purchaseShouldThrowIfUserIsNotUserType() {
        Account invalidUser = new Account(userId, "Invalid", AccountType.MERCHANT);

        assertThatThrownBy(() -> transactionService.purchase(invalidUser, merchant, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only USER accounts can purchase");
    }

    @Test
    void purchaseShouldThrowIfMerchantIsNotMerchantType() {
        Account invalidMerchant = new Account(merchantId, "Invalid", AccountType.USER);

        assertThatThrownBy(() -> transactionService.purchase(user, invalidMerchant, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only MERCHANT accounts can sell");
    }

    @Test
    void rechargeShouldSaveTransaction() {
        transactionService.recharge(user, BigDecimal.TEN);

        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void rechargeShouldThrowIfAccountIsNotUserType() {
        Account invalidUser = new Account(userId, "Invalid", AccountType.MERCHANT);

        assertThatThrownBy(() -> transactionService.recharge(invalidUser, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only USER accounts can be recharged");
    }

    @Test
    void calculateAccountBalanceShouldReturnCorrectValue() {
        when(transactionRepository.sumCredits(any(), eq(TransactionService.DEFAULT_CURRENCY)))
                .thenReturn(new BigDecimal("100.00"));
        when(transactionRepository.sumDebits(any(), eq(TransactionService.DEFAULT_CURRENCY)))
                .thenReturn(new BigDecimal("30.00"));

        BigDecimal balance = transactionService.calculateAccountBalance(userId.toString());

        assertThat(balance).isEqualTo(new BigDecimal("70.00"));
    }
}
