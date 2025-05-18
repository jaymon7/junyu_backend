package com.bank.application.port.input;

import com.bank.application.port.input.dto.WithdrawMoneyCommand;
import com.bank.application.port.input.dto.WithdrawMoneyResponse;

public interface WithdrawMoneyUseCase {
    WithdrawMoneyResponse withdraw(WithdrawMoneyCommand command);
}
