package com.ramsesvidor.prepaidstore.api.dto.response;

import com.ramsesvidor.prepaidstore.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private String sku;
    private String name;

    public static ProductResponse from(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .sku(product.getSku())
                .name(product.getName())
                .build();
    }

}