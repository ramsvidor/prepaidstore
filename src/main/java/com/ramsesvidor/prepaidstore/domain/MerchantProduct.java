package com.ramsesvidor.prepaidstore.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(MerchantProductId.class)
@Table(name = "merchant_products")
public class MerchantProduct {

    @Id
    private UUID merchantId;

    @Id
    private String sku;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Integer stock;

    public BigDecimal getStockValue() {
        return multiplyPriceBy(stock);
    }

    public BigDecimal multiplyPriceBy(Integer quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

}