package com.bank.application.port.input.dto;

public record CreateAccountCommand(
        String accountHolderName
) {
}
