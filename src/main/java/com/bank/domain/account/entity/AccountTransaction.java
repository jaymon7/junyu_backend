package com.bank.domain.account.entity;

import com.bank.domain.account.valueobject.AccountId;
import com.bank.domain.account.valueobject.AccountTransactionId;
import com.bank.domain.account.valueobject.Money;
import com.bank.domain.common.entity.BaseEntity;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public abstract class AccountTransaction extends BaseEntity<AccountTransactionId> {
    protected AccountId accountId;
    protected Money balance;
    protected Money amount;
    protected ZonedDateTime transactionAt;
}
