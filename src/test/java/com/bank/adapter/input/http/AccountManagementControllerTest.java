package com.bank.adapter.input.http;

import com.bank.application.exception.AccountNotFoundException;
import com.bank.application.port.input.CreateAccountUseCase;
import com.bank.application.port.input.DestroyAccountUseCase;
import com.bank.application.port.input.dto.CreateAccountCommand;
import com.bank.application.port.input.dto.CreateAccountResponse;
import com.bank.application.port.input.dto.DestroyAccountCommand;
import com.bank.domain.account.entity.Account;
import com.bank.domain.account.exception.AccountStatusInvalidException;
import com.bank.domain.account.valueobject.AccountNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountManagementController.class)
class AccountManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAccountUseCase createAccountUseCase;

    @MockBean
    private DestroyAccountUseCase destroyAccountUseCase;

    @Test
    @DisplayName("계좌 생성 요청 성공")
    void 계좌_생성_성공() throws Exception {
        // given
        String accountHolderName = "홍길동";
        Account account = Account.createAccount(accountHolderName);
        given(createAccountUseCase.createAccount(new CreateAccountCommand(accountHolderName)))
                .willReturn(
                        new CreateAccountResponse(
                                account.getId(),
                                account.getAccountNumber(),
                                account.getAccountHolderName(),
                                account.getBalance()
                        )
                );

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountHolderName\": \"" + accountHolderName + "\"}"))
                .andDo(print());

        // then
        actions.andExpect(status().isCreated())
                .andExpect(handler().handlerType(AccountManagementController.class))
                .andExpect(handler().methodName("postAccount"))
                .andExpect(jsonPath("$.accountHolderName").value(accountHolderName))
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber().value()))
                .andExpect(jsonPath("$.balance").value(account.getBalance().amount().intValue()));
    }

    @Test
    @DisplayName("계좌 생성 요청 실패 - 계좌 소유자 이름 누락")
    void 계좌_생성_실패_계좌소유자이름누락() throws Exception {
        // given
        String requestBody = "{}";

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountManagementController.class))
                .andExpect(handler().methodName("postAccount"));
    }

    @Test
    @DisplayName("계좌 삭제 요청 성공")
    void 계좌_삭제_성공() throws Exception {
        // given
        AccountNumber accountNumber = AccountNumber.generateAccountNumber();

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/accounts/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(handler().handlerType(AccountManagementController.class))
                .andExpect(handler().methodName("deleteAccount"));
    }

    @Test
    @DisplayName("계좌 삭제 요청 실패 - 없는 계좌")
    void 계좌_삭제_실패_없는계좌() throws Exception {
        // given
        AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        doThrow(new AccountNotFoundException("계좌를 찾을 수 없습니다."))
                .when(destroyAccountUseCase).destroyAccount(new DestroyAccountCommand(accountNumber));

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/accounts/" + accountNumber))
                .andDo(print());

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(handler().handlerType(AccountManagementController.class))
                .andExpect(handler().methodName("deleteAccount"));
    }

    @Test
    @DisplayName("계좌 삭제 요청 실패 - 이미 삭제된 계좌")
    void 계좌_삭제_실패_이미삭제된계좌() throws Exception {
        // given
        AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        doThrow(new AccountStatusInvalidException("계좌 상태가 유효하지 않습니다."))
                .when(destroyAccountUseCase).destroyAccount(new DestroyAccountCommand(accountNumber));

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/accounts/" + accountNumber))
                .andDo(print());

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(AccountManagementController.class))
                .andExpect(handler().methodName("deleteAccount"));
    }
}