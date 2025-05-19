package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountId;
import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;

public record CreateAccountResponse(
        AccountId accountId,
        AccountNumber accountNumber,
        String accountHolderName,
        Money balance
) {}
