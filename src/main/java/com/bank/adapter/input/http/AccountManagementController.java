package com.bank.adapter.input.http;

import com.bank.adapter.input.http.mapper.CreateAccountApiRequest;
import com.bank.adapter.input.http.mapper.CreateAccountApiResponse;
import com.bank.adapter.input.http.openapi.AccountManagementApi;
import com.bank.application.port.input.CreateAccountUseCase;
import com.bank.application.port.input.DestroyAccountUseCase;
import com.bank.application.port.input.dto.CreateAccountCommand;
import com.bank.application.port.input.dto.CreateAccountResponse;
import com.bank.application.port.input.dto.DestroyAccountCommand;
import com.bank.domain.account.valueobject.AccountNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountManagementController implements AccountManagementApi {

    private final CreateAccountUseCase createAccountUseCase;
    private final DestroyAccountUseCase destroyAccountUseCase;

    @Override
    public ResponseEntity<CreateAccountApiResponse> postAccount(CreateAccountApiRequest request) {
        CreateAccountResponse response = createAccountUseCase.createAccount(
                new CreateAccountCommand(
                        request.accountHolderName()
                )
        );

        return ResponseEntity.created(null).body(
                new CreateAccountApiResponse(
                        response.account().getId().getValue().toString(),
                        response.account().getAccountHolderName(),
                        response.account().getAccountNumber().value(),
                        response.account().getBalance().amount().intValue()
                )
        );
    }

    @Override
    public ResponseEntity<?> deleteAccount(String accountNumber) {
        destroyAccountUseCase.destroyAccount(
                new DestroyAccountCommand(
                        AccountNumber.of(accountNumber)
                )
        );

        return ResponseEntity.ok(null);
    }
}
