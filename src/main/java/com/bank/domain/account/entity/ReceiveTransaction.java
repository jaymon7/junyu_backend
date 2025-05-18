package com.bank.domain.account.entity;

import com.bank.domain.account.valueobject.AccountId;
import com.bank.domain.account.valueobject.AccountTransactionId;
import com.bank.domain.account.valueobject.Money;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Optional;

@Getter
@ToString
public class ReceiveTransaction extends AccountTransaction {

    private AccountId senderAccountId;

    @Builder
    public ReceiveTransaction(
            AccountTransactionId id,
            AccountId accountId,
            AccountId senderAccountId,
            Money balance,
            Money amount,
            ZonedDateTime transactionAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.senderAccountId = senderAccountId;
        this.balance = Optional.ofNullable(balance).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.amount = Optional.ofNullable(amount).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.transactionAt = transactionAt;
    }

    public static ReceiveTransaction recordReceiveTransaction(AccountId receiverAccountId, AccountId senderAccountId, Money amount, Money balance) {
        return ReceiveTransaction.builder()
                .id(AccountTransactionId.generateId())
                .accountId(receiverAccountId)
                .senderAccountId(senderAccountId)
                .amount(Money.of(amount.amount()))
                .balance(Money.of(balance.amount()))
                .transactionAt(ZonedDateTime.now())
                .build();
    }
}
