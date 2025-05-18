package com.bank.domain.account.entity;

public enum AccountStatus {
    ACTIVE,
    DESTROYED;

    public boolean canDeposit() {
        return this == ACTIVE;
    }

    public boolean canWithdraw() {
        return this == ACTIVE;
    }

    public boolean canTransfer() {
        return this == ACTIVE;
    }
}
