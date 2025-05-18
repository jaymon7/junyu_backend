package com.bank.application.port.output.persistence;

import com.bank.application.port.input.dto.AccountTransactionRetrieveResponse;
import com.bank.domain.account.entity.Account;
import com.bank.domain.account.valueobject.AccountId;
import com.bank.domain.account.valueobject.AccountNumber;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface AccountRepository {

    void create(Account account);

    Optional<Account> findByAccountNumber(AccountNumber accountNumber);

    void update(Account account);

    Page<AccountTransactionRetrieveResponse.Transaction> findAllAccountTransaction(AccountId accountId, int page, int size);
}
