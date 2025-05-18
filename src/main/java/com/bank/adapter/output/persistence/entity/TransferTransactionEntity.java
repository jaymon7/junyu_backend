package com.bank.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue(value = "2")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferTransactionEntity extends AccountTransactionEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id")
    private AccountEntity receiverAccount;

    @Column(name = "transfer_fee")
    private BigDecimal transferFee;

    @Builder
    public TransferTransactionEntity(
            UUID id,
            BigDecimal transferFee,
            BigDecimal balance,
            BigDecimal amount,
            ZonedDateTime transactionAt) {
        this.id = id;
        this.transferFee = transferFee;
        this.balance = balance;
        this.amount = amount;
        this.transactionAt = transactionAt;
    }

    public void assignReceiverAccount(AccountEntity receiverAccount) {
        this.receiverAccount = receiverAccount;
    }
}
