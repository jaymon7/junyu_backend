package com.bank.application.port.input;

import com.bank.application.port.input.dto.TransferMoneyCommand;
import com.bank.application.port.input.dto.TransferMoneyResponse;

public interface TransferMoneyUseCase {
    TransferMoneyResponse transfer(TransferMoneyCommand command);
}
