package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.common.exception.NotFoundException;
import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.MerchantProduct;
import com.ramsesvidor.prepaidstore.domain.MerchantProductId;
import com.ramsesvidor.prepaidstore.domain.Product;
import com.ramsesvidor.prepaidstore.infrastructure.MerchantProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantService {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ProductService productService;
    private final MerchantProductRepository merchantProductRepository;

    public Account findById(@NotBlank String merchantId) {
        return accountService.findMerchantById(merchantId);
    }

    public MerchantProduct findMerchantProduct(@NotBlank String merchantId, String sku) {
        return findMerchantProduct(findById(merchantId), sku);
    }

    public MerchantProduct findMerchantProduct(@Valid Account merchant, String sku) {
        return merchantProductRepository.findById(new MerchantProductId(merchant.getId(), sku))
                .orElseThrow(() -> new NotFoundException("Merchant doesn't have this product: " + sku));
    }

    public List<MerchantProduct> listProductsByMerchantId(@NotBlank String merchantId) {
        return merchantProductRepository.findByMerchantId(UUID.fromString(merchantId));
    }

    public BigDecimal getBalance(@NotBlank String merchandId) {
        return transactionService.calculateAccountBalance(merchandId);
    }

    @Transactional
    public MerchantProduct addMerchantProduct(@NotBlank String merchantId,
                                              @NotBlank String sku,
                                              @DecimalMin("0.01") BigDecimal price,
                                              @Min(1) Integer stock) {
        Account merchant = findById(merchantId);

        Product product = productService.findById(sku);

        MerchantProductId id = new MerchantProductId(merchant.getId(), product.getSku());

        if (merchantProductRepository.existsById(id)) {
            throw new IllegalArgumentException("Merchant already selling this product SKU: " + product.getSku());
        }

        MerchantProduct merchantProduct = new MerchantProduct(merchant.getId(), product.getSku(), price, stock);
        merchantProductRepository.save(merchantProduct);
        return merchantProduct;
    }

    @Transactional
    public void decreaseStock(@Valid MerchantProduct merchantProduct, @Min(1) Integer quantity) {
        if (merchantProduct.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock.");
        }

        merchantProduct.setStock(merchantProduct.getStock() - quantity);
        merchantProductRepository.save(merchantProduct);
    }

    @Scheduled(cron = "0 0 2 * * *") // Every day at 2AM
    public void reconcile() {
        accountService.findAllMerchants().forEach(merchant -> {
            BigDecimal totalStockValue = merchantProductRepository.findByMerchantId(merchant.getId())
                    .stream()
                    .map(MerchantProduct::getStockValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal balance = transactionService.calculateAccountBalance(merchant.getId().toString());

            log.info("Merchant [{}] - Expected Stock Value: ${}, Actual Balance: ${}",
                    merchant.getName(),
                    totalStockValue,
                    balance);
        });
    }

}
