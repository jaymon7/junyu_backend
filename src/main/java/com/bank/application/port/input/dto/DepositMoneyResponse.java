package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;

import java.time.ZonedDateTime;

public record DepositMoneyResponse(
        AccountNumber accountNumber,
        Money amount,
        Money balance,
        ZonedDateTime depositAt
) {
}
