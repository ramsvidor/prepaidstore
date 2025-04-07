package com.ramsesvidor.prepaidstore.api;

import com.ramsesvidor.prepaidstore.application.AccountService;
import com.ramsesvidor.prepaidstore.common.exception.NotFoundException;
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

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        AccountService accountService() {
            return mock(AccountService.class);
        }
    }

    private final String defaultName = "Test user";
    private final Account defaultAccount = new Account(UUID.randomUUID(), defaultName, AccountType.USER);

    @Test
    void createAccountShouldReturn201Created() throws Exception {
        when(accountService.createAccount(defaultName, AccountType.USER)).thenReturn(defaultAccount);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + defaultName + "\", \"accountType\":\"USER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(defaultName));
    }

    @Test
    void createAccountWithInvalidRequestShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllAccountsShouldReturn200() throws Exception {
        when(accountService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk());
    }

    @Test
    void findAccountByIdShouldReturn200() throws Exception {
        when(accountService.findById(defaultAccount.getId().toString())).thenReturn(defaultAccount);

        mockMvc.perform(get("/api/accounts/{id}", defaultAccount.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(defaultName));
    }

    @Test
    void findAccountByIdWhenNotFoundShouldReturn404() throws Exception {
        String id = UUID.randomUUID().toString();
        when(accountService.findById(id)).thenThrow(new NotFoundException("Account not found: " + id));

        mockMvc.perform(get("/api/accounts/{id}", id))
                .andExpect(status().isNotFound());
    }
}
