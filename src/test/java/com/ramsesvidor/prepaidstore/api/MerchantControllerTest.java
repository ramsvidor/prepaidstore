package com.ramsesvidor.prepaidstore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramsesvidor.prepaidstore.api.dto.request.AddMerchantProductRequest;
import com.ramsesvidor.prepaidstore.api.dto.response.MerchantProductResponse;
import com.ramsesvidor.prepaidstore.application.MerchantService;
import com.ramsesvidor.prepaidstore.domain.MerchantProduct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MerchantController.class)
class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        MerchantService merchantService() {
            return mock(MerchantService.class);
        }
    }

    private final UUID defaultMerchantId = UUID.randomUUID();
    private final String defaultSku = "SKU123";
    private final BigDecimal defaultPrice = new BigDecimal("99.99");
    private final Integer defaultStock = 10;
    private final AddMerchantProductRequest request = new AddMerchantProductRequest(
            defaultSku, defaultPrice, defaultStock
    );
    private final MerchantProductResponse response = new MerchantProductResponse(
            defaultMerchantId, defaultSku, defaultPrice, defaultStock
    );
    private final MerchantProduct defaultMerchantProduct = new MerchantProduct(
            defaultMerchantId, defaultSku, defaultPrice, defaultStock
    );

    @Test
    void addMerchantProductShouldReturn201Created() throws Exception {
        when(merchantService.addMerchantProduct(
                defaultMerchantId.toString(), defaultSku, defaultPrice, defaultStock
        )).thenReturn(defaultMerchantProduct);

        mockMvc.perform(post("/api/merchants/{merchantId}/products", defaultMerchantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value(defaultSku))
                .andExpect(jsonPath("$.price").value(defaultPrice))
                .andExpect(jsonPath("$.stock").value(defaultStock));
    }

    @Test
    void addMerchantProductWithInvalidRequestShouldReturn400() throws Exception {
        UUID merchantId = UUID.randomUUID();

        // Missing fields in request
        String invalidRequestJson = "{}";

        mockMvc.perform(post("/api/merchants/{merchantId}/products", merchantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }
}
