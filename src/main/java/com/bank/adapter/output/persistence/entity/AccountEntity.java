package com.bank.adapter.output.persistence.entity;

import com.bank.domain.account.entity.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"transactions"})
public class AccountEntity extends BaseEntity<UUID> {

    @Column(nullable = false)
    @ColumnDefault("0")
    private BigDecimal balance;

    @Enumerated(EnumType.ORDINAL)
    private AccountStatus status;

    @Column(name = "account_number", nullable = false, updatable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(name = "withdraw_limit_amount", nullable = false)
    private BigDecimal withdrawLimitAmount;

    @Column(name = "transfer_limit_amount", nullable = false)
    private BigDecimal transferLimitAmount;

    @Column(name = "transfer_fee_rate", nullable = false)
    private BigDecimal transferFeeRate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "destroyed_at")
    private ZonedDateTime destroyedAt;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Set<AccountTransactionEntity> transactions = new HashSet<>();

    @Builder
    public AccountEntity(UUID id, BigDecimal balance, AccountStatus status, String accountNumber, String accountHolderName, BigDecimal withdrawLimitAmount, BigDecimal transferLimitAmount, BigDecimal transferFeeRate, ZonedDateTime createdAt, ZonedDateTime destroyedAt, Collection<AccountTransactionEntity> transactions) {
        this.id = id;
        this.balance = balance;
        this.status = status;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.withdrawLimitAmount = withdrawLimitAmount;
        this.transferLimitAmount = transferLimitAmount;
        this.transferFeeRate = transferFeeRate;
        this.createdAt = createdAt;
        this.destroyedAt = destroyedAt;
        this.transactions = new HashSet<>(transactions);
    }

    public void updateAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public void updateBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void changeStatus(AccountStatus status) {
        this.status = status;
    }

    public void updateWithdrawLimitAmount(BigDecimal withdrawLimitAmount) {
        this.withdrawLimitAmount = withdrawLimitAmount;
    }

    public void updateTransferLimitAmount(BigDecimal transferLimitAmount) {
        this.transferLimitAmount = transferLimitAmount;
    }

    public void updateTransferFeeRate(BigDecimal transferFeeRate) {
        this.transferFeeRate = transferFeeRate;
    }

    public void updateDestroyedAt(ZonedDateTime destroyedAt) {
        this.destroyedAt = destroyedAt;
    }

    public void addTransaction(AccountTransactionEntity transaction) {
        this.transactions.add(transaction);
        transaction.assignAccount(this);
    }
}
