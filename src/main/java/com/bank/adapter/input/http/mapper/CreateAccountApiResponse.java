package com.bank.adapter.input.http.mapper;

public record CreateAccountApiResponse(
        String accountId,
        String accountHolderName,
        String accountNumber,
        Integer balance
) {
}
