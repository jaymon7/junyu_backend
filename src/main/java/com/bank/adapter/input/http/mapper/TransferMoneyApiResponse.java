package com.bank.adapter.input.http.mapper;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(title = "계좌 이체 응답", description = "계좌 이체 응답 정보")
public record TransferMoneyApiResponse(
        @Schema(description = "송금 계좌번호", example = "123-2415-1234")
        String senderAccountNumber,
        @Schema(description = "수신 계좌번호", example = "123-2415-1234")
        String receiverAccountNumber,
        @Schema(description = "잔액", example = "100000")
        Integer balance,
        @Schema(description = "송금 금액", example = "10000")
        Integer amount,
        @Schema(description = "송금 수수료", example = "1000")
        Integer transferFee,
        @Schema(description = "송금 일시", example = "2023-10-01T10:00:00Z")
        ZonedDateTime transferAt
) {
}
