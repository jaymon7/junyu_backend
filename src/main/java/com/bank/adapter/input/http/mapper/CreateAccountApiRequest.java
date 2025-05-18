package com.bank.adapter.input.http.mapper;

import jakarta.validation.constraints.NotNull;

public record CreateAccountApiRequest(
        @NotNull(message = "계좌 소유자 이름은 필수입니다.")
        String accountHolderName
) {
}
