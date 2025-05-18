package com.bank.adapter.input.http.mapper;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransferMoneyApiRequest(
        @NotNull(message = "송금계좌번호는 필수입니다.")
        @Schema(
                description = "송금계좌번호",
                example = "123-456-7890",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String senderAccountNumber,
        @NotNull(message = "수신계좌번호는 필수입니다.")
        @Schema(
                description = "수신계좌번호",
                example = "098-765-4321",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String receiverAccountNumber,
        @NotNull(message = "송금액은 필수입니다.")
        @Min(value = 1, message = "송금액은 1원 이상이어야 합니다.")
        @Schema(
                description = "송금액",
                example = "10000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer amount
) {
}
