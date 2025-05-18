package com.bank.adapter.input.http.mapper;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateAccountApiResponse(
        @Schema(
                description = "계좌 소유자 이름",
                example = "홍길동"
        )
        String accountHolderName,
        @Schema(
                description = "계좌번호",
                example = "123-456-7890"
        )
        String accountNumber,
        @Schema(
                description = "계좌 잔액",
                example = "10000"
        )
        Integer balance
) {
}
