package com.ramsesvidor.prepaidstore.api.dto.response;

import com.ramsesvidor.prepaidstore.domain.Account;
import com.ramsesvidor.prepaidstore.domain.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {

    private UUID id;
    private String name;
    private AccountType accountType;

    public static AccountResponse from(Account account) {
        if (account == null) {
            return null;
        }

        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .accountType(account.getAccountType())
                .build();
    }

}
