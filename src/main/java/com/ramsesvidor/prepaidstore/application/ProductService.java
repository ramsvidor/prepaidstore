package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.common.exception.NotFoundException;
import com.ramsesvidor.prepaidstore.domain.Product;
import com.ramsesvidor.prepaidstore.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(String sku) {
        return productRepository.findById(sku)
                .orElseThrow(() -> new NotFoundException("Product not found: " + sku));
    }

    @Transactional
    public Product addProduct(String sku, String name) {
        if (productRepository.existsById(sku)) {
            throw new IllegalArgumentException("Product with SKU already exists: " + sku);
        }

        return productRepository.save(new Product(sku, name));
    }

}