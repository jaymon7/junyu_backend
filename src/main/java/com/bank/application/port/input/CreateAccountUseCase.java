package com.bank.application.port.input;


import com.bank.application.port.input.dto.CreateAccountCommand;
import com.bank.application.port.input.dto.CreateAccountResponse;

public interface CreateAccountUseCase {
    CreateAccountResponse createAccount(CreateAccountCommand command);
}
