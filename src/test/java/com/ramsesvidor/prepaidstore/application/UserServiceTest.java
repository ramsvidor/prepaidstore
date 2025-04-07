package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.AccountType;
import com.ramsesvidor.prepaidstore.domain.MerchantProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private AccountService accountService;
    private TransactionService transactionService;
    private MerchantService merchantService;
    private UserService userService;

    private final UUID userId = UUID.randomUUID();
    private final UUID merchantId = UUID.randomUUID();
    private final String sku = "SKU123";
    private final BigDecimal amount = BigDecimal.valueOf(100);

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        transactionService = mock(TransactionService.class);
        merchantService = mock(MerchantService.class);
        userService = new UserService(accountService, transactionService, merchantService);
    }

    @Test
    void findByIdShouldReturnUser() {
        Account user = new Account(userId, "User", AccountType.USER);
        when(accountService.findUserById(userId.toString())).thenReturn(user);

        Account result = userService.findById(userId.toString());

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getAccountType()).isEqualTo(AccountType.USER);
    }

    @Test
    void findAllShouldReturnUsers() {
        when(accountService.findAllUsers()).thenReturn(Collections.emptyList());

        assertThat(userService.findAll()).isEmpty();
    }

    @Test
    void getBalanceShouldReturnBalance() {
        when(transactionService.calculateAccountBalance(userId.toString())).thenReturn(amount);

        BigDecimal balance = userService.getBalance(userId.toString());

        assertThat(balance).isEqualTo(amount);
    }

    @Test
    void rechargeShouldInvokeTransactionRecharge() {
        Account user = new Account(userId, "User", AccountType.USER);
        when(accountService.findUserById(userId.toString())).thenReturn(user);

        userService.recharge(userId.toString(), amount);

        verify(transactionService).recharge(user, amount);
    }

    @Test
    void purchaseShouldSucceedWhenBalanceSufficient() {
        Account user = new Account(userId, "User", AccountType.USER);
        Account merchant = new Account(merchantId, "Merchant", AccountType.MERCHANT);
        MerchantProduct merchantProduct = new MerchantProduct(merchantId, sku, BigDecimal.TEN, 100);

        when(accountService.findUserById(userId.toString())).thenReturn(user);
        when(merchantService.findById(merchantId.toString())).thenReturn(merchant);
        when(merchantService.findMerchantProduct(merchant, sku)).thenReturn(merchantProduct);
        when(transactionService.calculateAccountBalance(userId.toString())).thenReturn(BigDecimal.valueOf(200));

        userService.purchase(userId.toString(), merchantId.toString(), sku, 5);

        verify(merchantService).decreaseStock(merchantProduct, 5);
        verify(transactionService).purchase(user, merchant, BigDecimal.valueOf(50));
    }

    @Test
    void purchaseShouldThrowWhenBalanceInsufficient() {
        Account user = new Account(userId, "User", AccountType.USER);
        Account merchant = new Account(merchantId, "Merchant", AccountType.MERCHANT);
        MerchantProduct merchantProduct = new MerchantProduct(merchantId, sku, BigDecimal.TEN, 100);

        when(accountService.findUserById(userId.toString())).thenReturn(user);
        when(merchantService.findById(merchantId.toString())).thenReturn(merchant);
        when(merchantService.findMerchantProduct(merchant, sku)).thenReturn(merchantProduct);
        when(transactionService.calculateAccountBalance(userId.toString())).thenReturn(BigDecimal.valueOf(10));

        assertThatThrownBy(() -> userService.purchase(userId.toString(), merchantId.toString(), sku, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient balance");
    }
}
