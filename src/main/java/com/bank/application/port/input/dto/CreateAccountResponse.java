package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;

public record CreateAccountResponse(
        AccountNumber accountNumber,
        String accountHolderName,
        Money balance
) {}
