package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.common.exception.NotFoundException;
import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.AccountType;
import com.ramsesvidor.prepaidstore.infrastructure.AccountRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(@NotBlank String name, @NotNull AccountType accountType) {
        return accountRepository.save(new Account(UUID.randomUUID(), name, accountType));
    }

    public Account findById(@NotBlank String id) {
        return findById(UUID.fromString(id));
    }

    public Account findUserById(@NotBlank String id) {
        return findAccountByIdAndType(id, AccountType.USER);
    }

    public Account findMerchantById(@NotBlank String id) {
        return findAccountByIdAndType(id, AccountType.MERCHANT);
    }

    public Account findById(@NotBlank UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findAllUsers() {
        return findAllByType(AccountType.USER);
    }

    public List<Account> findAllMerchants() {
        return findAllByType(AccountType.MERCHANT);
    }

    private Account findAccountByIdAndType(String id, AccountType accountType) {
        Account account = findById(UUID.fromString(id));

        if (!account.getAccountType().equals(accountType)) {
            throw new NotFoundException("No account with id " + id + " and type " + accountType + " found.");
        }

        return account;
    }

    private List<Account> findAllByType(AccountType accountType) {
        return accountRepository.findAll()
                .stream()
                .filter(account -> account.getAccountType().equals(accountType))
                .toList();
    }

}