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
public class TransferTransaction extends AccountTransaction {

    private AccountId receiverAccountId;
    private Money transferFee;

    @Builder
    public TransferTransaction(
            AccountTransactionId id,
            AccountId accountId,
            AccountId receiverAccountId,
            Money transferFee,
            Money balance,
            Money amount,
            ZonedDateTime transactionAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.receiverAccountId = receiverAccountId;
        this.transferFee = Optional.ofNullable(transferFee).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.balance = Optional.ofNullable(balance).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.amount = Optional.ofNullable(amount).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.transactionAt = transactionAt;
    }

    public static TransferTransaction recordTransferTransaction(AccountId senderAccountId, AccountId receiverAccountId, Money transferFee, Money amount, Money balance) {
        return TransferTransaction.builder()
                .id(AccountTransactionId.generateId())
                .accountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .transferFee(Money.of(transferFee.amount()))
                .amount(Money.of(amount.amount()))
                .balance(Money.of(balance.amount()))
                .transactionAt(ZonedDateTime.now())
                .build();
    }
}
