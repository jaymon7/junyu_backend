package com.bank.adapter.output.persistence.entity;

import com.bank.domain.account.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue(value = "1")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ReceiveTransactionEntity extends AccountTransactionEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id")
    private AccountEntity senderAccount;

    @Builder
    public ReceiveTransactionEntity(
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

    public void assignSenderAccount(AccountEntity senderAccount) {
        this.senderAccount = senderAccount;
    }
}
