package com.bank.adapter.input.http.openapi;

import com.bank.adapter.input.http.mapper.CreateAccountApiRequest;
import com.bank.adapter.input.http.mapper.CreateAccountApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

public interface AccountManagementApi {

    @Operation(
            summary = "[API V1] 계좌 생성",
            operationId = "postAccount",
            description = "계좌를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "계좌 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
            },
            tags = {"API V1"}
    )
    @RequestMapping(
            method = POST,
            value = "/api/v1/accounts",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<CreateAccountApiResponse> postAccount(
            @Parameter(description = "계좌 생성 요청 정보")
            @Valid @RequestBody CreateAccountApiRequest request
    );


    @Operation(
            summary = "[API V1] 계좌 삭제",
            operationId = "deleteAccount",
            description = "계좌를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "계좌 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
            },
            tags = {"API V1"}
    )
    @RequestMapping(
            method = DELETE,
            value = "/api/v1/accounts/{accountNumber}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<?> deleteAccount(
            @Parameter(description = "계좌번호")
            @PathVariable String accountNumber
    );
}
