package com.bank.domain.account.valueobject;

import com.bank.domain.common.valueobject.BaseId;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AccountId extends BaseId<UUID> {

    public AccountId(UUID value) {
        this.value = value;
    }

    public static AccountId of(UUID value) {
        return new AccountId(value);
    }

    public static AccountId generateId() {
        return new AccountId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
