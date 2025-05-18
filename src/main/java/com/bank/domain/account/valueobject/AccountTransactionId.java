package com.bank.domain.account.valueobject;

import com.bank.domain.common.valueobject.BaseId;

import java.util.UUID;

public class AccountTransactionId extends BaseId<UUID> {

    public AccountTransactionId(UUID value) {
        this.value = value;
    }

    public static AccountTransactionId of(UUID value) {
        return new AccountTransactionId(value);
    }

    public static AccountTransactionId generateId() {
        return new AccountTransactionId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
