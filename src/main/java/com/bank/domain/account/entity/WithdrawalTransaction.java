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
public class WithdrawalTransaction extends AccountTransaction {

    @Builder
    public WithdrawalTransaction(
            AccountTransactionId id,
            AccountId accountId,
            Money balance,
            Money amount,
            ZonedDateTime transactionAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.balance = Optional.ofNullable(balance).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.amount = Optional.ofNullable(amount).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.transactionAt = transactionAt;
    }

    public static WithdrawalTransaction recordWithdrawalTransaction(AccountId withdrawalAccountId, Money amount, Money balance) {
        return WithdrawalTransaction.builder()
                .id(AccountTransactionId.generateId())
                .accountId(withdrawalAccountId)
                .amount(Money.of(amount.amount()))
                .balance(Money.of(balance.amount()))
                .transactionAt(ZonedDateTime.now())
                .build();
    }
}
