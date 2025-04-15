package com.ramsesvidor.prepaidstore.infrastructure;

import com.ramsesvidor.prepaidstore.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}