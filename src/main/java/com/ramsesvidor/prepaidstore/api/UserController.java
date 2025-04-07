package com.ramsesvidor.prepaidstore.api;

import com.ramsesvidor.prepaidstore.api.dto.request.PurchaseRequest;
import com.ramsesvidor.prepaidstore.api.dto.request.RechargeRequest;
import com.ramsesvidor.prepaidstore.api.dto.response.AccountResponse;
import com.ramsesvidor.prepaidstore.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<AccountResponse> findAll() {
        return userService.findAll().stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{userId}")
    public AccountResponse findById(@PathVariable("userId") String userId) {
        return AccountResponse.from(userService.findById(userId));
    }

    @GetMapping("/{userId}/balance")
    public BigDecimal getBalance(@PathVariable("userId") String userId) {
        return userService.getBalance(userId);
    }

    @PostMapping("/{userId}/recharge")
    public void recharge(@PathVariable("userId") String userId, @RequestBody @Valid RechargeRequest request) {
        userService.recharge(userId, request.getAmount());
    }

    @PostMapping("/{userId}/purchase")
    public void purchase(@PathVariable("userId") String userId, @RequestBody @Valid PurchaseRequest request) {
        userService.purchase(userId, request.getMerchantId(), request.getSku(), request.getQuantity());
    }

}