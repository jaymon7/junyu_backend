package com.bank.adapter.input.http.mapper;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateAccountApiRequest(
        @NotNull(message = "계좌 소유자 이름은 필수입니다.")
        @Schema(
                description = "계좌 소유자 이름",
                example = "홍길동",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String accountHolderName
) {
}
