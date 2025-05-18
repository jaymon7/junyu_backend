package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;

public record DestroyAccountCommand(
        AccountNumber accountNumber
) {
}
