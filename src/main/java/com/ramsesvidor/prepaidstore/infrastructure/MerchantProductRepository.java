package com.ramsesvidor.prepaidstore.infrastructure;

import com.ramsesvidor.prepaidstore.domain.MerchantProduct;
import com.ramsesvidor.prepaidstore.domain.MerchantProductId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantProductRepository extends JpaRepository<MerchantProduct, MerchantProductId> {

    List<MerchantProduct> findByMerchantId(UUID merchantId);

}