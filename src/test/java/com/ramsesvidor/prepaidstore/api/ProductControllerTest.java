package com.ramsesvidor.prepaidstore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramsesvidor.prepaidstore.api.dto.request.AddProductRequest;
import com.ramsesvidor.prepaidstore.application.ProductService;
import com.ramsesvidor.prepaidstore.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        ProductService productService() {
            return mock(ProductService.class);
        }
    }

    private final String defaultSku = "SKU123";
    private final String defaultName = "Test Product";
    private final AddProductRequest request = new AddProductRequest(defaultSku, defaultName);
    private final Product defaultProduct = new Product(defaultSku, defaultName);

    @Test
    void addProductShouldReturn201Created() throws Exception {
        when(productService.addProduct(defaultSku, defaultName)).thenReturn(defaultProduct);

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value(defaultSku))
                .andExpect(jsonPath("$.name").value(defaultName));
    }

    @Test
    void addProductWithInvalidRequestShouldReturn400() throws Exception {
        String invalidRequestJson = "{}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllProductsShouldReturn200() throws Exception {
        when(productService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void findProductBySkuWhenNotFoundShouldReturn404() throws Exception {
        when(productService.findById(defaultSku)).thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/api/products/{sku}", defaultSku))
                .andExpect(status().isInternalServerError());
    }
}
