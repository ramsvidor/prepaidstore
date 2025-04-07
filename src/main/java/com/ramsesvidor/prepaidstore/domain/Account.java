package com.ramsesvidor.prepaidstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

}