package com.bank.application.port.input;

import com.bank.application.port.input.dto.AccountTransactionRetrieveCommand;
import com.bank.application.port.input.dto.AccountTransactionRetrieveResponse;

public interface AccountTransactionRetrieveUseCase {
    AccountTransactionRetrieveResponse retrieveAccountTransactions(AccountTransactionRetrieveCommand command);
}
