package com.bank.adapter.output.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue(value = "0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositTransactionEntity extends AccountTransactionEntity {

    @Builder
    public DepositTransactionEntity(
            UUID id,
            BigDecimal balance,
            BigDecimal amount,
            ZonedDateTime transactionAt
    ) {
        this.id = id;
        this.balance = balance;
        this.amount = amount;
        this.transactionAt = transactionAt;
    }
}
