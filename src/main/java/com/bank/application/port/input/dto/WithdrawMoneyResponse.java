package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;

import java.time.ZonedDateTime;

public record WithdrawMoneyResponse(
        AccountNumber accountNumber,
        Money balance,
        Money amount,
        ZonedDateTime withdrawAt
) {
}
