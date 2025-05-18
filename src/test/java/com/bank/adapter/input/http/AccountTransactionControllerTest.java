package com.bank.adapter.input.http;

import com.bank.application.exception.AccountNotFoundException;
import com.bank.application.port.input.AccountTransactionRetrieveUseCase;
import com.bank.application.port.input.DepositMoneyUseCase;
import com.bank.application.port.input.TransferMoneyUseCase;
import com.bank.application.port.input.WithdrawMoneyUseCase;
import com.bank.application.port.input.dto.*;
import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountTransactionController.class)
class AccountTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountTransactionRetrieveUseCase transactionRetrieveUseCase;

    @MockBean
    private DepositMoneyUseCase depositMoneyUseCase;

    @MockBean
    private TransferMoneyUseCase transferMoneyUseCase;

    @MockBean
    private WithdrawMoneyUseCase withdrawMoneyUseCase;

    @Test
    @DisplayName("계좌 입금 요청 성공")
    void 계좌_입금_요청_성공() throws Exception {
        // given
        AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        Integer amount = 1000;
        ZonedDateTime depositAt = ZonedDateTime.now();
        given(depositMoneyUseCase.deposit(new DepositMoneyCommand(accountNumber, Money.of(new BigDecimal(amount)))))
                .willReturn(new DepositMoneyResponse(
                        accountNumber,
                        Money.of(new BigDecimal(amount)),
                        Money.of(new BigDecimal(amount)),
                        depositAt
        ));

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"accountNumber\": \"" + accountNumber.value() + "\", \"amount\": " + amount + "}"))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsDeposit"))
                .andExpect(jsonPath("$.accountNumber").value(accountNumber.value()))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.balance").value(amount))
                .andExpect(jsonPath("$.depositAt").exists());
    }

    @Test
    @DisplayName("계좌 입금 요청 실패 - 계좌 번호 누락")
    void 계좌_입금_요청_실패_계좌번호누락() throws Exception {
        // given
        String requestBody = "{\"amount\": 1000}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsDeposit"));
    }

    @Test
    @DisplayName("계좌 입금 요청 실패 - 금액 누락")
    void 계좌_입금_요청_실패_금액누락() throws Exception {
        // given
        String requestBody = "{\"accountNumber\": \"1234567890\"}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsDeposit"));
    }

    @Test
    @DisplayName("계좌 입금 요청 실패 - 금액이 1원 미만")
    void 계좌_입금_요청_실패_금액이1원미만() throws Exception {
        // given
        String requestBody = "{\"accountNumber\": \"1234567890\", \"amount\": 0}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsDeposit"));
    }

    @Test
    @DisplayName("계좌 이체 요청 성공")
    void 계좌_이체_요청_성공() throws Exception {
        // given
        AccountNumber senderAccountNumber = AccountNumber.generateAccountNumber();
        AccountNumber receiverAccountNumber = AccountNumber.generateAccountNumber();
        Integer amount = 1000;
        ZonedDateTime transferAt = ZonedDateTime.now();
        given(transferMoneyUseCase.transfer(
                new TransferMoneyCommand(
                        senderAccountNumber,
                        receiverAccountNumber,
                        Money.of(new BigDecimal(amount))
                )
        )).willReturn(
                new TransferMoneyResponse(
                        senderAccountNumber,
                        receiverAccountNumber,
                        Money.of(new BigDecimal(amount)),
                        Money.of(new BigDecimal(amount)),
                        Money.of(new BigDecimal(100)), // Assuming no transfer fee for simplicity
                        transferAt
                )
        );

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"senderAccountNumber\": \"" + senderAccountNumber.value() + "\", \"receiverAccountNumber\": \"" + receiverAccountNumber.value() + "\", \"amount\": " + amount + "}"))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsTransfer"))
                .andExpect(jsonPath("$.senderAccountNumber").value(senderAccountNumber.value()))
                .andExpect(jsonPath("$.receiverAccountNumber").value(receiverAccountNumber.value()))
                .andExpect(jsonPath("$.balance").value(amount))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.transferFee").value(100))
                .andExpect(jsonPath("$.transferAt").exists());
    }

    @Test
    @DisplayName("계좌 이체 요청 실패 - 송금 계좌 번호 누락")
    void 계좌_이체_요청_실패_송금계좌번호누락() throws Exception {
        // given
        String requestBody = "{\"receiverAccountNumber\": \"1234567890\", \"amount\": 1000}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsTransfer"));
    }

    @Test
    @DisplayName("계좌 이체 요청 실패 - 수신 계좌 번호 누락")
    void 계좌_이체_요청_실패_수신계좌번호누락() throws Exception {
        // given
        String requestBody = "{\"senderAccountNumber\": \"1234567890\", \"amount\": 1000}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsTransfer"));
    }

    @Test
    @DisplayName("계좌 이체 요청 실패 - 금액 누락")
    void 계좌_이체_요청_실패_금액누락() throws Exception {
        // given
        String requestBody = "{\"senderAccountNumber\": \"1234567890\", \"receiverAccountNumber\": \"0987654321\"}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsTransfer"));
    }

    @Test
    @DisplayName("계좌 이체 요청 실패 - 금액이 1원 미만")
    void 계좌_이체_요청_실패_금액이1원미만() throws Exception {
        // given
        String requestBody = "{\"senderAccountNumber\": \"1234567890\", \"receiverAccountNumber\": \"0987654321\", \"amount\": 0}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsTransfer"));
    }

    @Test
    @DisplayName("계좌 출금 요청 성공")
    void 계좌_출금_요청_성공() throws Exception {
        // given
        AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        Integer amount = 1000;
        ZonedDateTime withdrawAt = ZonedDateTime.now();
        given(withdrawMoneyUseCase.withdraw(new WithdrawMoneyCommand(accountNumber, Money.of(new BigDecimal(amount)))))
                .willReturn(new WithdrawMoneyResponse(
                        accountNumber,
                        Money.ZERO,
                        Money.of(new BigDecimal(amount)),
                        withdrawAt
                ));

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/withdrawal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"accountNumber\": \"" + accountNumber.value() + "\", \"amount\": " + amount + "}"))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsWithdrawal"))
                .andExpect(jsonPath("$.accountNumber").value(accountNumber.value()))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.withdrawalAt").exists());
    }

    @Test
    @DisplayName("계좌 출금 요청 실패 - 계좌 번호 누락")
    void 계좌_출금_요청_실패_계좌번호누락() throws Exception {
        // given
        String requestBody = "{\"amount\": 1000}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/withdrawal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsWithdrawal"));
    }

    @Test
    @DisplayName("계좌 출금 요청 실패 - 금액 누락")
    void 계좌_출금_요청_실패_금액누락() throws Exception {
        // given
        String requestBody = "{\"accountNumber\": \"1234567890\"}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/withdrawal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsWithdrawal"));
    }

    @Test
    @DisplayName("계좌 출금 요청 실패 - 금액이 1원 미만")
    void 계좌_출금_요청_실패_금액이1원미만() throws Exception {
        // given
        String requestBody = "{\"accountNumber\": \"1234567890\", \"amount\": 0}";

        // when
        ResultActions actions = mockMvc.perform(
                        post("/api/v1/accounts/withdrawal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("postAccountsWithdrawal"));
    }

    @Test
    @DisplayName("계좌 거래 내역 조회 요청 성공")
    void 계좌_거래내역_조회_요청_성공() throws Exception {
        // given
        AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        int page = 0;
        int size = 10;
        given(transactionRetrieveUseCase.retrieveAccountTransactions(new AccountTransactionRetrieveCommand(accountNumber, page, size)))
                .willReturn(new AccountTransactionRetrieveResponse(Page.empty()));

        // when
        ResultActions actions = mockMvc.perform(
                        get("/api/v1/accounts/" + accountNumber.value() + "/transactions")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size)))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("getAccountTransaction"))
                .andExpect(jsonPath("$.transactions").exists())
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    @DisplayName("계좌 거래 내역 조회 요청 실패 - 계좌 파기 상태")
    void 계좌_거래내역_조회_요청_실패_계좌파기상태() throws Exception {
        // given
        AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        int page = 0;
        int size = 10;
        given(transactionRetrieveUseCase.retrieveAccountTransactions(new AccountTransactionRetrieveCommand(accountNumber, page, size)))
                .willThrow(new AccountNotFoundException("계좌를 찾을 수 없습니다."));

        // when
        ResultActions actions = mockMvc.perform(
                        get("/api/v1/accounts/" + accountNumber.value() + "/transactions")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size)))
                .andDo(print());

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(handler().handlerType(AccountTransactionController.class))
                .andExpect(handler().methodName("getAccountTransaction"));
    }
}