package com.bank.adapter.input.http.mapper;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DepositMoneyApiRequest(
        @NotNull(message = "계좌번호는 필수입니다.")
        String accountNumber,
        @NotNull(message = "입금액은 필수입니다.")
        @Min(value = 1, message = "입금액은 1원 이상이어야 합니다.")
        Integer amount
) {
}
