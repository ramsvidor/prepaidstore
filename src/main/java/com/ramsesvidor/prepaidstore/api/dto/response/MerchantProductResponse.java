package com.ramsesvidor.prepaidstore.api.dto.response;

import com.ramsesvidor.prepaidstore.domain.MerchantProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantProductResponse {

    private UUID merchantId;
    private String sku;
    private BigDecimal price;
    private Integer stock;

    public static MerchantProductResponse from(MerchantProduct merchantProduct) {
        if (merchantProduct == null) {
            return null;
        }

        return MerchantProductResponse.builder()
                .merchantId(merchantProduct.getMerchantId())
                .sku(merchantProduct.getSku())
                .price(merchantProduct.getPrice())
                .stock(merchantProduct.getStock())
                .build();
    }

}
