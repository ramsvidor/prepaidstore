package com.ramsesvidor.prepaidstore.api;

import com.ramsesvidor.prepaidstore.api.dto.request.AccountRequest;
import com.ramsesvidor.prepaidstore.api.dto.response.AccountResponse;
import com.ramsesvidor.prepaidstore.application.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createUser(@RequestBody @Valid AccountRequest accountRequest) {
        AccountResponse response = AccountResponse.from(
                accountService.createAccount(accountRequest.getName(), accountRequest.getAccountType())
        );

        return ResponseEntity
                .created(URI.create("/api/accounts/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll().stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{accountId}")
    public AccountResponse findById(@PathVariable("accountId") String accountId) {
        return AccountResponse.from(accountService.findById(accountId));
    }

}
