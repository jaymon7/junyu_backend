package com.bank.adapter.input.http.mapper;

import com.bank.application.port.input.dto.AccountTransactionRetrieveResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;
import java.util.List;

@Schema(title = "계좌간 거래 내역 조회 응답", description = "계좌간 거래 내역 조회 응답 정보")
public record RetrieveAccountTransactionApiResponse(
        @Schema(description = "계좌 거래 내역", example = "[{\"receiverAccountNumber\": \"123-2415-1234\", \"amount\": 1000, \"balance\": 9000, \"transactionAt\": \"2023-10-01T10:00:00Z\"}]")
        List<Transaction> transactions,
        @Schema(description = "페이징 정보")
        PaginationApiResponse pagination
) {
    @Schema(title = "계좌 거래 내역", description = "계좌 거래 내역 정보")
    public record Transaction(
            @Schema(description = "송금 계좌번호", example = "123-2415-1234")
            String senderAccountNumber,
            @Schema(description = "수취 계좌번호", example = "123-2415-1234")
            String receiverAccountNumber,
            @Schema(description = "거래 금액", example = "1000")
            Integer amount,
            @Schema(description = "거래 일시", example = "2023-10-01T10:00:00Z")
            ZonedDateTime transactionAt
    ) {
    }

    public static RetrieveAccountTransactionApiResponse of(
            Page<AccountTransactionRetrieveResponse.Transaction> page
    ) {
        return new RetrieveAccountTransactionApiResponse(
                page.getContent().stream()
                        .map(transaction -> new Transaction(
                                transaction.senderAccountNumber().value(),
                                transaction.receiverAccountNumber().value(),
                                transaction.amount().amount().intValue(),
                                transaction.transactionAt()
                        ))
                        .toList(),
                PaginationApiResponse.of(page)
        );
    }
}
