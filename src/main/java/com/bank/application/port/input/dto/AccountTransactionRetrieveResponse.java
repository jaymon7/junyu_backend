package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;

public record AccountTransactionRetrieveResponse(
        Page<Transaction> accountTransactionPage
) {
    public record Transaction(
            AccountNumber senderAccountNumber,
            AccountNumber receiverAccountNumber,
            Money amount,
            ZonedDateTime transactionAt
    ) {}
}
