package com.ramsesvidor.prepaidstore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramsesvidor.prepaidstore.api.dto.request.PurchaseRequest;
import com.ramsesvidor.prepaidstore.api.dto.request.RechargeRequest;
import com.ramsesvidor.prepaidstore.application.UserService;
import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        UserService userService() {
            return mock(UserService.class);
        }
    }

    private final String defaultUserId = UUID.randomUUID().toString();
    private final Account defaultAccount = new Account(UUID.randomUUID(), "Test User", AccountType.USER);

    @Test
    void findAllUsersShouldReturn200() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void findUserByIdShouldReturn200() throws Exception {
        when(userService.findById(defaultUserId)).thenReturn(defaultAccount);

        mockMvc.perform(get("/api/users/{userId}", defaultUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getBalanceShouldReturn200() throws Exception {
        when(userService.getBalance(defaultUserId)).thenReturn(new BigDecimal("100.00"));

        mockMvc.perform(get("/api/users/{userId}/balance", defaultUserId))
                .andExpect(status().isOk());
    }

    @Test
    void rechargeShouldReturn200() throws Exception {
        RechargeRequest request = new RechargeRequest(new BigDecimal("50.00"));
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/users/{userId}/recharge", defaultUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void rechargeWithInvalidRequestShouldReturn400() throws Exception {
        String invalidRequestJson = "{}";

        mockMvc.perform(post("/api/users/{userId}/recharge", defaultUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void purchaseShouldReturn200() throws Exception {
        PurchaseRequest request = new PurchaseRequest(UUID.randomUUID().toString(), "SKU123", 1);
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/users/{userId}/purchase", defaultUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void purchaseWithInvalidRequestShouldReturn400() throws Exception {
        String invalidRequestJson = "{}";

        mockMvc.perform(post("/api/users/{userId}/purchase", defaultUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }
}
