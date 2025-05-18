package com.bank.adapter.output.persistence.mapper;

import com.bank.adapter.output.persistence.entity.*;
import com.bank.domain.account.entity.*;
import com.bank.domain.account.valueobject.*;
import org.springframework.stereotype.Component;

@Component
public class AccountEntityMapper {

    public AccountEntity map(Account account) {
        return AccountEntity.builder()
                .id(account.getId().getValue())
                .accountNumber(account.getAccountNumber().value())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance().amount())
                .status(account.getStatus())
                .withdrawLimitAmount(account.getWithdrawLimitAmount().amount())
                .transferLimitAmount(account.getTransferLimitAmount().amount())
                .transferFeeRate(account.getTransferFeeRate().percentage())
                .createdAt(account.getCreatedAt())
                .destroyedAt(account.getDestroyedAt())
                .transactions(account.getTransactions().stream().map(this::map).toList())
                .build();
    }

    public AccountTransactionEntity map(AccountTransaction transaction) {
        if (transaction instanceof DepositTransaction) {
            return DepositTransactionEntity.builder()
                    .id(transaction.getId().getValue())
                    .balance(transaction.getBalance().amount())
                    .amount(transaction.getAmount().amount())
                    .transactionAt(transaction.getTransactionAt())
                    .build();
        } else if(transaction instanceof ReceiveTransaction) {
            return ReceiveTransactionEntity.builder()
                    .id(transaction.getId().getValue())
                    .balance(transaction.getBalance().amount())
                    .amount(transaction.getAmount().amount())
                    .transactionAt(transaction.getTransactionAt())
                    .build();
        } else if (transaction instanceof TransferTransaction) {
            return TransferTransactionEntity.builder()
                    .id(transaction.getId().getValue())
                    .transferFee(((TransferTransaction) transaction).getTransferFee().amount())
                    .balance(transaction.getBalance().amount())
                    .amount(transaction.getAmount().amount())
                    .transactionAt(transaction.getTransactionAt())
                    .build();
        } else if (transaction instanceof WithdrawalTransaction) {
            return WithdrawalTransactionEntity.builder()
                    .id(transaction.getId().getValue())
                    .balance(transaction.getBalance().amount())
                    .amount(transaction.getAmount().amount())
                    .transactionAt(transaction.getTransactionAt())
                    .build();
        }

        return null;
    }

    public Account map(AccountEntity accountEntity) {
        return Account.builder()
                .id(AccountId.of(accountEntity.getId()))
                .accountNumber(AccountNumber.of(accountEntity.getAccountNumber()))
                .accountHolderName(accountEntity.getAccountHolderName())
                .balance(Money.of(accountEntity.getBalance()))
                .status(accountEntity.getStatus())
                .withdrawLimitAmount(Money.of(accountEntity.getWithdrawLimitAmount()))
                .transferLimitAmount(Money.of(accountEntity.getTransferLimitAmount()))
                .transferFeeRate(FeeRate.of(accountEntity.getTransferFeeRate()))
                .createdAt(accountEntity.getCreatedAt())
                .destroyedAt(accountEntity.getDestroyedAt())
                .transactions(accountEntity.getTransactions().stream().map(this::map).toList())
                .build();
    }

    public AccountTransaction map(AccountTransactionEntity transactionEntity) {
        if (transactionEntity instanceof DepositTransactionEntity) {
            return DepositTransaction.builder()
                    .id(AccountTransactionId.of(transactionEntity.getId()))
                    .balance(Money.of(transactionEntity.getBalance()))
                    .amount(Money.of(transactionEntity.getAmount()))
                    .transactionAt(transactionEntity.getTransactionAt())
                    .build();
        } else if (transactionEntity instanceof ReceiveTransactionEntity) {
            return ReceiveTransaction.builder()
                    .id(AccountTransactionId.of(transactionEntity.getId()))
                    .senderAccountId(AccountId.of(((ReceiveTransactionEntity) transactionEntity).getSenderAccount().getId()))
                    .balance(Money.of(transactionEntity.getBalance()))
                    .amount(Money.of(transactionEntity.getAmount()))
                    .transactionAt(transactionEntity.getTransactionAt())
                    .build();
        } else if (transactionEntity instanceof TransferTransactionEntity) {
            return TransferTransaction.builder()
                    .id(AccountTransactionId.of(transactionEntity.getId()))
                    .receiverAccountId(AccountId.of(((TransferTransactionEntity) transactionEntity).getReceiverAccount().getId()))
                    .balance(Money.of(transactionEntity.getBalance()))
                    .amount(Money.of(transactionEntity.getAmount()))
                    .transactionAt(transactionEntity.getTransactionAt())
                    .build();
        } else if (transactionEntity instanceof WithdrawalTransactionEntity) {
            return WithdrawalTransaction.builder()
                    .id(AccountTransactionId.of(transactionEntity.getId()))
                    .balance(Money.of(transactionEntity.getBalance()))
                    .amount(Money.of(transactionEntity.getAmount()))
                    .transactionAt(transactionEntity.getTransactionAt())
                    .build();
        }

        return null;
    }
}
