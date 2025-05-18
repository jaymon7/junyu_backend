package com.bank.adapter.output.persistence.repository;

import com.bank.adapter.output.persistence.entity.AccountTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AccountTransactionJpaRepository extends JpaRepository<AccountTransactionEntity, UUID> {
    @Query(value = """
                SELECT a
                FROM AccountTransactionEntity a
                WHERE a.account.id = :accountId
                AND (TYPE(a) = TransferTransactionEntity OR TYPE(a) = ReceiveTransactionEntity)
            """)
    Page<AccountTransactionEntity> findAllTransferOrReceiveTransactionByAccountId(UUID accountId, Pageable pageable);
}
