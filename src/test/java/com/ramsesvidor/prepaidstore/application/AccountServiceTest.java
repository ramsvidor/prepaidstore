package com.ramsesvidor.prepaidstore.application;

import com.ramsesvidor.prepaidstore.common.exception.NotFoundException;
import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.AccountType;
import com.ramsesvidor.prepaidstore.infrastructure.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private final UUID defaultId = UUID.randomUUID();
    private final String defaultName = "Test User";
    private final Account defaultAccount = new Account(defaultId, defaultName, AccountType.USER);

    @Test
    void createAccountShouldSaveAndReturnAccount() {
        when(accountRepository.save(any(Account.class))).thenReturn(defaultAccount);

        Account created = accountService.createAccount(defaultName, AccountType.USER);

        assertThat(created.getName()).isEqualTo(defaultName);
        assertThat(created.getAccountType()).isEqualTo(AccountType.USER);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void findByIdShouldReturnAccount() {
        when(accountRepository.findById(defaultId)).thenReturn(Optional.of(defaultAccount));

        Account found = accountService.findById(defaultId);

        assertThat(found.getId()).isEqualTo(defaultId);
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(accountRepository.findById(defaultId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(defaultId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void findAllShouldReturnAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of(defaultAccount));

        List<Account> accounts = accountService.findAll();

        assertThat(accounts).hasSize(1);
    }

    @Test
    void findAllUsersShouldFilterOnlyUsers() {
        when(accountRepository.findAll()).thenReturn(List.of(defaultAccount,
                new Account(UUID.randomUUID(), "Merchant", AccountType.MERCHANT)));

        List<Account> users = accountService.findAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.getFirst().getAccountType()).isEqualTo(AccountType.USER);
    }

    @Test
    void findAllMerchantsShouldFilterOnlyMerchants() {
        when(accountRepository.findAll()).thenReturn(List.of(defaultAccount,
                new Account(UUID.randomUUID(), "Merchant", AccountType.MERCHANT)));

        List<Account> merchants = accountService.findAllMerchants();

        assertThat(merchants).hasSize(1);
        assertThat(merchants.getFirst().getAccountType()).isEqualTo(AccountType.MERCHANT);
    }

    @Test
    void findUserByIdShouldReturnUser() {
        Account userAccount = new Account(defaultId, "User Test", AccountType.USER);
        when(accountRepository.findById(defaultId)).thenReturn(Optional.of(userAccount));

        Account user = accountService.findUserById(defaultId.toString());

        assertThat(user.getAccountType()).isEqualTo(AccountType.USER);
    }

    @Test
    void findMerchantByIdShouldThrowIfWrongType() {
        Account userAccount = new Account(defaultId, "User Test", AccountType.USER);
        when(accountRepository.findById(defaultId)).thenReturn(Optional.of(userAccount));

        assertThatThrownBy(() -> accountService.findMerchantById(defaultId.toString()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No account with id " + defaultId);
    }

}
