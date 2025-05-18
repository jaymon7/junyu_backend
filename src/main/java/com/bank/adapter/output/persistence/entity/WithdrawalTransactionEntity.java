package com.bank.adapter.output.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue(value = "3")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class WithdrawalTransactionEntity extends AccountTransactionEntity {

    @Builder
    public WithdrawalTransactionEntity(
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
