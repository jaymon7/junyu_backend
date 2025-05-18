package com.bank.adapter.input.http.mapper;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DepositMoneyApiRequest(
        @NotNull(message = "계좌번호는 필수입니다.")
        @Schema(
                description = "계좌번호",
                example = "123-456-7890",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String accountNumber,
        @NotNull(message = "입금액은 필수입니다.")
        @Min(value = 1, message = "입금액은 1원 이상이어야 합니다.")
        @Schema(
                description = "입금액",
                example = "10000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer amount
) {
}
