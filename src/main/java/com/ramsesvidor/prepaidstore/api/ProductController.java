package com.ramsesvidor.prepaidstore.api;

import com.ramsesvidor.prepaidstore.api.dto.request.AddProductRequest;
import com.ramsesvidor.prepaidstore.api.dto.response.ProductResponse;
import com.ramsesvidor.prepaidstore.application.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll().stream().map(ProductResponse::from).toList();
    }

    @GetMapping("/{sku}")
    public ProductResponse findById(@PathVariable("sku") String sku) {
        return ProductResponse.from(productService.findById(sku));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody @Valid AddProductRequest request) {
        ProductResponse response = ProductResponse.from(productService.addProduct(request.getSku(), request.getName()));
        return ResponseEntity.created(URI.create("/api/products/" + response.getSku())).body(response);
    }

}