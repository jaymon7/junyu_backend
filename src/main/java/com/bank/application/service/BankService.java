package com.bank.application.service;

import com.bank.application.exception.AccountNotFoundException;
import com.bank.application.port.input.*;
import com.bank.application.port.input.dto.*;
import com.bank.application.port.output.persistence.AccountRepository;
import com.bank.domain.account.entity.*;
import com.bank.domain.account.valueobject.AccountNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankService implements CreateAccountUseCase, DestroyAccountUseCase, DepositMoneyUseCase, TransferMoneyUseCase, WithdrawMoneyUseCase, AccountTransactionRetrieveUseCase {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public CreateAccountResponse createAccount(CreateAccountCommand command) {
        Account account = Account.createAccount(command.accountHolderName());
        accountRepository.create(account);

        return new CreateAccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getBalance()
        );
    }

    @Override
    @Transactional
    public void destroyAccount(DestroyAccountCommand command) {
        Account account = getAccount(command.accountNumber());
        account.destroy();

        accountRepository.update(account);
    }

    @Override
    @Transactional
    public DepositMoneyResponse deposit(DepositMoneyCommand command) {
        Account account = getAccount(command.accountNumber());
        DepositTransaction transaction = account.deposit(command.amount());

        accountRepository.update(account);

        return new DepositMoneyResponse(
                account.getAccountNumber(),
                command.amount(),
                account.getBalance(),
                transaction.getTransactionAt()
        );
    }

    @Override
    @Transactional
    public TransferMoneyResponse transfer(TransferMoneyCommand command) {
        Account senderAccount = getAccount(command.senderAccountNumber());
        Account receiverAccount = getAccount(command.receiverAccountNumber());

        TransferTransaction transferTransaction = senderAccount.transfer(
                receiverAccount.getId(),
                command.amount()
        );

        receiverAccount.receive(
                senderAccount.getId(),
                command.amount()
        );

        accountRepository.update(senderAccount);
        accountRepository.update(receiverAccount);

        return new TransferMoneyResponse(
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber(),
                senderAccount.getBalance(),
                command.amount(),
                transferTransaction.getTransferFee(),
                transferTransaction.getTransactionAt()
        );
    }

    @Override
    @Transactional
    public WithdrawMoneyResponse withdraw(WithdrawMoneyCommand command) {
        Account account = getAccount(command.accountNumber());
        WithdrawalTransaction withdraw = account.withdraw(command.amount());

        accountRepository.update(account);

        return new WithdrawMoneyResponse(
                account.getAccountNumber(),
                account.getBalance(),
                command.amount(),
                withdraw.getTransactionAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AccountTransactionRetrieveResponse retrieveAccountTransactions(AccountTransactionRetrieveCommand command) {
        Account account = getAccount(command.accountNumber());

        if (account.getStatus() == AccountStatus.DESTROYED) {
            throw new AccountNotFoundException("계좌를 찾을 수 없습니다.");
        }

        return new AccountTransactionRetrieveResponse(
                accountRepository.findAllTransferTransactionOrReceiveTransaction(
                        account.getId(),
                        command.page(),
                        command.size()
                )
        );
    }

    private Account getAccount(AccountNumber accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("계좌를 찾을 수 없습니다."));
    }
}
