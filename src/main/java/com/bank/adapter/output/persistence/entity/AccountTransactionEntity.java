package com.bank.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_transaction")
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.INTEGER)
public abstract class AccountTransactionEntity extends BaseEntity<UUID> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    protected AccountEntity account;

    @Column(nullable = false)
    protected BigDecimal balance;

    @Column(nullable = false)
    protected BigDecimal amount;

    @Column(name = "transaction_at", nullable = false)
    protected ZonedDateTime transactionAt;

    protected void assignAccount(AccountEntity account) {
        this.account = account;
    }
}
