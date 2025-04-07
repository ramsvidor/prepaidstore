package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.common.exception.NotFoundException;
import com.ramsesvidor.prepaidstore.domain.Product;
import com.ramsesvidor.prepaidstore.infrastructure.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private final String sku = "SKU123";
    private final String name = "Test Product";
    private Product product;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        product = new Product(sku, name);
    }

    @Test
    void findAllShouldReturnProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        assertThat(productService.findAll()).hasSize(1);
    }

    @Test
    void findByIdShouldReturnProduct() {
        when(productRepository.findById(sku)).thenReturn(Optional.of(product));

        Product found = productService.findById(sku);

        assertThat(found.getSku()).isEqualTo(sku);
        assertThat(found.getName()).isEqualTo(name);
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(productRepository.findById(sku)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(sku))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void addProductShouldSaveSuccessfully() {
        when(productRepository.existsById(sku)).thenReturn(false);

        Product saved = productService.addProduct(sku, name);

        assertThat(saved.getSku()).isEqualTo(sku);
        assertThat(saved.getName()).isEqualTo(name);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProductShouldThrowIfExists() {
        when(productRepository.existsById(sku)).thenReturn(true);

        assertThatThrownBy(() -> productService.addProduct(sku, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }
}
