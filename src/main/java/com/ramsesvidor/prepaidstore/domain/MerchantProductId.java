package com.ramsesvidor.prepaidstore.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantProductId implements Serializable {

    @NotNull
    private UUID merchantId;

    @NotBlank
    private String sku;

}