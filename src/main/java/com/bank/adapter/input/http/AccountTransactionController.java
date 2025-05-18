package com.bank.adapter.input.http;

import com.bank.adapter.input.http.mapper.*;
import com.bank.adapter.input.http.openapi.AccountTransactionApi;
import com.bank.application.port.input.AccountTransactionRetrieveUseCase;
import com.bank.application.port.input.DepositMoneyUseCase;
import com.bank.application.port.input.TransferMoneyUseCase;
import com.bank.application.port.input.WithdrawMoneyUseCase;
import com.bank.application.port.input.dto.*;
import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class AccountTransactionController implements AccountTransactionApi {

    private final AccountTransactionRetrieveUseCase transactionRetrieveUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    private final TransferMoneyUseCase transferMoneyUseCase;
    private final WithdrawMoneyUseCase withdrawMoneyUseCase;

    @Override
    public ResponseEntity<DepositMoneyApiResponse> postAccountsDeposit(DepositMoneyApiRequest request) {
        DepositMoneyResponse depositResponse = depositMoneyUseCase.deposit(
                new DepositMoneyCommand(
                        AccountNumber.of(request.accountNumber()),
                        Money.of(new BigDecimal(request.amount()))
                )
        );

        return ResponseEntity.ok(
                new DepositMoneyApiResponse(
                        depositResponse.accountNumber().value(),
                        depositResponse.amount().amount().intValue(),
                        depositResponse.balance().amount().intValue(),
                        depositResponse.depositAt()
                )
        );
    }

    @Override
    public ResponseEntity<TransferMoneyApiResponse> postAccountsTransfer(TransferMoneyApiRequest request) {
        TransferMoneyResponse transferResponse = transferMoneyUseCase.transfer(
                new TransferMoneyCommand(
                        AccountNumber.of(request.senderAccountNumber()),
                        AccountNumber.of(request.receiverAccountNumber()),
                        Money.of(new BigDecimal(request.amount()))
                )
        );

        return ResponseEntity.ok(
                new TransferMoneyApiResponse(
                        transferResponse.senderAccountNumber().value(),
                        transferResponse.receiverAccountNumber().value(),
                        transferResponse.amount().amount().intValue(),
                        transferResponse.balance().amount().intValue(),
                        transferResponse.transferFee().amount().intValue(),
                        transferResponse.transferAt()
                )
        );
    }

    @Override
    public ResponseEntity<WithdrawalMoneyApiResponse> postAccountsWithdrawal(DepositMoneyApiRequest request) {
        WithdrawMoneyResponse withdrawResponse = withdrawMoneyUseCase.withdraw(
                new WithdrawMoneyCommand(
                        AccountNumber.of(request.accountNumber()),
                        Money.of(new BigDecimal(request.amount()))
                )
        );

        return ResponseEntity.ok(
                new WithdrawalMoneyApiResponse(
                        withdrawResponse.accountNumber().value(),
                        withdrawResponse.balance().amount().intValue(),
                        withdrawResponse.amount().amount().intValue(),
                        withdrawResponse.withdrawAt()
                )
        );
    }

    @Override
    public ResponseEntity<RetrieveAccountTransactionApiResponse> getAccountTransaction(String accountNumber, int page, int size) {
        AccountTransactionRetrieveResponse transactionResponse = transactionRetrieveUseCase.retrieveAccountTransactions(
                new AccountTransactionRetrieveCommand(
                        AccountNumber.of(accountNumber),
                        page,
                        size
                )
        );

        return ResponseEntity.ok(
                RetrieveAccountTransactionApiResponse.of(transactionResponse.accountTransactionPage())
        );
    }
}
