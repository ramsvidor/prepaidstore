package com.ramsesvidor.prepaidstore.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    @NotBlank
    private String merchantId;

    @NotBlank
    private String sku;

    @NotNull
    @Min(1)
    private Integer quantity;

}