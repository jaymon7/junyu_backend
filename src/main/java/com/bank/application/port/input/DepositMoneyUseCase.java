package com.bank.application.port.input;

import com.bank.application.port.input.dto.DepositMoneyCommand;
import com.bank.application.port.input.dto.DepositMoneyResponse;

public interface DepositMoneyUseCase {
    DepositMoneyResponse deposit(DepositMoneyCommand command);
}
