package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.common.exception.NotFoundException;
import com.ramsesvidor.prepaidstore.domain.*;
import com.ramsesvidor.prepaidstore.infrastructure.MerchantProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class MerchantServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ProductService productService;

    @Mock
    private MerchantProductRepository merchantProductRepository;

    @InjectMocks
    private MerchantService merchantService;

    private UUID merchantId;
    private String sku;
    private Account merchantAccount;
    private Product product;
    private MerchantProduct merchantProduct;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        merchantId = UUID.randomUUID();
        sku = "SKU123";
        merchantAccount = new Account(merchantId, "Merchant", AccountType.MERCHANT);
        product = new Product(sku, "Test Product");
        merchantProduct = new MerchantProduct(merchantId, sku, new BigDecimal("99.99"), 10);
    }

    @Test
    void findByIdShouldReturnMerchant() {
        when(accountService.findMerchantById(merchantId.toString())).thenReturn(merchantAccount);

        Account found = merchantService.findById(merchantId.toString());

        assertThat(found).isEqualTo(merchantAccount);
    }

    @Test
    void findMerchantProductShouldReturnProduct() {
        MerchantProductId id = new MerchantProductId(merchantId, sku);
        when(merchantProductRepository.findById(id)).thenReturn(Optional.of(merchantProduct));

        MerchantProduct found = merchantService.findMerchantProduct(merchantAccount, sku);

        assertThat(found).isEqualTo(merchantProduct);
    }

    @Test
    void findMerchantProductShouldThrowIfNotFound() {
        MerchantProductId id = new MerchantProductId(merchantId, sku);
        when(merchantProductRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> merchantService.findMerchantProduct(merchantAccount, sku))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Merchant doesn't have this product");
    }

    @Test
    void listProductsByMerchantIdShouldReturnProducts() {
        when(merchantProductRepository.findByMerchantId(merchantId)).thenReturn(Collections.singletonList(merchantProduct));

        assertThat(merchantService.listProductsByMerchantId(merchantId.toString())).hasSize(1);
    }

    @Test
    void getBalanceShouldReturnBalance() {
        BigDecimal balance = new BigDecimal("100.00");
        when(transactionService.calculateAccountBalance(merchantId.toString())).thenReturn(balance);

        BigDecimal result = merchantService.getBalance(merchantId.toString());

        assertThat(result).isEqualTo(balance);
    }

    @Test
    void addMerchantProductShouldAddSuccessfully() {
        when(accountService.findMerchantById(merchantId.toString())).thenReturn(merchantAccount);
        when(productService.findById(sku)).thenReturn(product);
        when(merchantProductRepository.existsById(any())).thenReturn(false);

        MerchantProduct added = merchantService.addMerchantProduct(merchantId.toString(), sku, new BigDecimal("99.99"), 10);

        assertThat(added.getSku()).isEqualTo(sku);
        assertThat(added.getStock()).isEqualTo(10);
    }

    @Test
    void addMerchantProductShouldThrowIfAlreadySelling() {
        when(accountService.findMerchantById(merchantId.toString())).thenReturn(merchantAccount);
        when(productService.findById(sku)).thenReturn(product);
        when(merchantProductRepository.existsById(any())).thenReturn(true);

        assertThatThrownBy(() -> merchantService.addMerchantProduct(merchantId.toString(), sku, new BigDecimal("99.99"), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already selling");
    }

    @Test
    void decreaseStockShouldSucceed() {
        merchantProduct.setStock(10);

        merchantService.decreaseStock(merchantProduct, 5);

        assertThat(merchantProduct.getStock()).isEqualTo(5);
    }

    @Test
    void decreaseStockShouldThrowIfNotEnoughStock() {
        merchantProduct.setStock(2);

        assertThatThrownBy(() -> merchantService.decreaseStock(merchantProduct, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not enough stock");
    }
}
