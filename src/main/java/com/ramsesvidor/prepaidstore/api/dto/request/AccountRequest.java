package com.ramsesvidor.prepaidstore.api.dto.request;

import com.ramsesvidor.prepaidstore.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank
    private String name;

    @NotNull
    private AccountType accountType;

}
