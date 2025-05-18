package com.bank.application.port.input;

import com.bank.application.port.input.dto.DestroyAccountCommand;

public interface DestroyAccountUseCase {
    void destroyAccount(DestroyAccountCommand command);
}
