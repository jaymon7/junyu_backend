package com.bank.adapter.input.http.openapi;

import com.bank.adapter.input.http.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

public interface AccountTransactionApi {

    @Operation(
            summary = "[API V1] 계좌 입금",
            operationId = "postAccountDeposit",
            description = "계좌에 입금합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "입금 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
            },
            tags = {"API V1"}
    )
    @RequestMapping(
            method = POST,
            value = "/api/v1/accounts/deposit",
            produces = {APPLICATION_JSON_VALUE},
            consumes = {APPLICATION_JSON_VALUE}
    )
    ResponseEntity<DepositMoneyApiResponse> postAccountsDeposit(
            @Parameter(description = "입금 정보")
            @Valid @RequestBody DepositMoneyApiRequest request
    );

    @Operation(
            summary = "[API V1] 계좌 이체",
            operationId = "postAccountTransfer",
            description = "계좌에서 다른 계좌로 이체합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이체 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
            },
            tags = {"API V1"}
    )
    @RequestMapping(
            method = POST,
            value = "/api/v1/accounts/transfer",
            produces = {APPLICATION_JSON_VALUE},
            consumes = {APPLICATION_JSON_VALUE}
    )
    ResponseEntity<TransferMoneyApiResponse> postAccountsTransfer(
            @Parameter(description = "계좌 이체 정보")
            @Valid @RequestBody TransferMoneyApiRequest request
    );

    @Operation(
            summary = "[API V1] 계좌 출금",
            operationId = "postAccountWithdrawal",
            description = "계좌에서 출금합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "출금 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
            },
            tags = {"API V1"}
    )
    @RequestMapping(
            method = POST,
            value = "/api/v1/accounts/withdrawal",
            produces = {APPLICATION_JSON_VALUE},
            consumes = {APPLICATION_JSON_VALUE}
    )
    ResponseEntity<WithdrawalMoneyApiResponse> postAccountsWithdrawal(
            @Parameter(description = "계좌 출금 정보")
            @Valid @RequestBody DepositMoneyApiRequest request
    );

    @Operation(
            summary = "[API V1] 계좌 거래 내역 조회",
            operationId = "getAccountTransaction",
            description = "계좌의 거래 내역을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "거래 내역 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
            },
            tags = {"API V1"}
    )
    @RequestMapping(
            method = GET,
            value = "/api/v1/accounts/{accountNumber}/transactions"
    )
    ResponseEntity<RetrieveAccountTransactionApiResponse> getAccountTransaction(
            @Parameter(description = "계좌 거래 내역 조회 정보", example = "123-456-7890")
            @PathVariable(value = "accountNumber") String accountNumber,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 사이즈") @RequestParam(defaultValue = "20") int size
    );
}
