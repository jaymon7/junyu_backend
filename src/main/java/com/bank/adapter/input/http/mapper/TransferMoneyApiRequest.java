package com.bank.adapter.input.http.mapper;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransferMoneyApiRequest(
        @NotNull(message = "송금계좌번호는 필수입니다.")
        String senderAccountNumber,
        @NotNull(message = "수신계좌번호는 필수입니다.")
        String receiverAccountNumber,
        @NotNull(message = "송금액은 필수입니다.")
        @Min(value = 1, message = "송금액은 1원 이상이어야 합니다.")
        Integer amount
) {
}
