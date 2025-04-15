package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.MerchantProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final MerchantService merchantService;

    public Account findById(String userId) {
        return accountService.findUserById(userId);
    }

    public List<Account> findAll() {
        return accountService.findAllUsers();
    }

    public BigDecimal getBalance(String userId) {
        return transactionService.calculateAccountBalance(userId);
    }

    @Transactional
    public void recharge(String userId, BigDecimal amount) {
        transactionService.recharge(accountService.findUserById(userId), amount);
    }

    @Transactional
    public void purchase(String userId, String merchantId, String sku, int quantity) {
        Account user = findById(userId);
        Account merchant = merchantService.findById(merchantId);
        MerchantProduct merchantProduct = merchantService.findMerchantProduct(merchant, sku);
        BigDecimal totalAmount = merchantProduct.multiplyPriceBy(quantity);
        BigDecimal userBalance = transactionService.calculateAccountBalance(userId);

        if (userBalance.compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        merchantService.decreaseStock(merchantProduct, quantity);
        transactionService.purchase(user, merchant, totalAmount);
    }

}