package com.ramsesvidor.prepaidstore.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String name;

}