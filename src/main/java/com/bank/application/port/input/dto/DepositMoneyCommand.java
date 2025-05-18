package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;

public record DepositMoneyCommand(
        AccountNumber accountNumber,
        Money amount
) {
}
