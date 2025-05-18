package com.bank.application.port.input.dto;

import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "계좌 출금 요청", description = "계좌 출금 요청 정보")
public record WithdrawMoneyCommand(
        @Schema(description = "계좌번호", example = "123-2415-1234")
        AccountNumber accountNumber,
        @Schema(description = "출금 금액", example = "10000")
        Money amount
) {
}
