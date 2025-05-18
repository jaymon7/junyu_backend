package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;

import java.time.ZonedDateTime;

public record TransferMoneyResponse(
        AccountNumber senderAccountNumber,
        AccountNumber receiverAccountNumber,
        Money balance,
        Money amount,
        Money transferFee,
        ZonedDateTime transferAt
) {
}
