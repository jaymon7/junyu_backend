package com.bank.adapter.output.persistence.repository;

import com.bank.adapter.output.persistence.entity.AccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
