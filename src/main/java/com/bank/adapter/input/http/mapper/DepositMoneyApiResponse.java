package com.bank.adapter.input.http.mapper;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(title = "계좌 입금 응답", description = "계좌 입금 응답 정보")
public record DepositMoneyApiResponse(
        @Schema(description = "계좌번호", example = "123-2415-1234")
        String accountNumber,
        @Schema(description = "입금 금액", example = "10000")
        Integer amount,
        @Schema(description = "잔액", example = "100000")
        Integer balance,
        @Schema(description = "입금 일시", example = "2023-10-01T10:00:00Z")
        ZonedDateTime depositAt
) {
}
