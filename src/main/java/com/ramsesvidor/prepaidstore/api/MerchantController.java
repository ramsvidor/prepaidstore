package com.ramsesvidor.prepaidstore.api;

import com.ramsesvidor.prepaidstore.api.dto.request.AddMerchantProductRequest;
import com.ramsesvidor.prepaidstore.api.dto.response.AccountResponse;
import com.ramsesvidor.prepaidstore.api.dto.response.MerchantProductResponse;
import com.ramsesvidor.prepaidstore.application.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/merchants/{merchantId}")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public AccountResponse findById(@PathVariable("merchantId") String merchantId) {
        return AccountResponse.from(merchantService.findById(merchantId));
    }

    @GetMapping("/balance")
    public BigDecimal getBalance(@PathVariable("merchantId") String merchantId) {
        return merchantService.getBalance(merchantId);
    }

    @PostMapping("/products")
    public ResponseEntity<MerchantProductResponse> addMerchantProduct(@PathVariable("merchantId") String merchantId,
                                                                      @RequestBody @Valid AddMerchantProductRequest request) {
        MerchantProductResponse response = MerchantProductResponse.from(
                merchantService.addMerchantProduct(
                        merchantId, request.getSku(), request.getPrice(), request.getStock()
                )
        );

        return ResponseEntity
                .created(URI.create("/api/" + merchantId + "/products/" + response.getSku()))
                .body(response);
    }

    @GetMapping("/products")
    public List<MerchantProductResponse> listProductsByMerchantId(@PathVariable("merchantId") String merchantId) {
        return merchantService.listProductsByMerchantId(merchantId)
                .stream()
                .map(MerchantProductResponse::from)
                .toList();
    }

    @GetMapping("/products/{sku}")
    public MerchantProductResponse findProductByMerchantId(@PathVariable("merchantId") String merchantId,
                                                           @PathVariable("sku") String sku) {
        return MerchantProductResponse.from(merchantService.findMerchantProduct(merchantId, sku));
    }

}