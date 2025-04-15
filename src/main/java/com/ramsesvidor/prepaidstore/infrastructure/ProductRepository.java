package com.ramsesvidor.prepaidstore.infrastructure;

import com.ramsesvidor.prepaidstore.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}