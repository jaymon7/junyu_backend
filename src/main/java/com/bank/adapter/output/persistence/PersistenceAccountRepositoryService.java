package com.bank.adapter.output.persistence;

import com.bank.adapter.output.persistence.entity.AccountEntity;
import com.bank.adapter.output.persistence.entity.AccountTransactionEntity;
import com.bank.adapter.output.persistence.entity.ReceiveTransactionEntity;
import com.bank.adapter.output.persistence.entity.TransferTransactionEntity;
import com.bank.adapter.output.persistence.exception.AccountEntityNotFoundException;
import com.bank.adapter.output.persistence.mapper.AccountEntityMapper;
import com.bank.adapter.output.persistence.repository.AccountJpaRepository;
import com.bank.adapter.output.persistence.repository.AccountTransactionJpaRepository;
import com.bank.application.port.input.dto.AccountTransactionRetrieveResponse;
import com.bank.application.port.output.persistence.AccountRepository;
import com.bank.domain.account.entity.Account;
import com.bank.domain.account.entity.ReceiveTransaction;
import com.bank.domain.account.entity.TransferTransaction;
import com.bank.domain.account.valueobject.AccountId;
import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersistenceAccountRepositoryService implements AccountRepository {

    private final AccountEntityMapper mapper;
    private final AccountJpaRepository accountRepository;
    private final AccountTransactionJpaRepository transactionRepository;

    @Override
    public void create(Account account) {
        accountRepository.save(mapper.map(account));
    }

    @Override
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber.value())
                .map(mapper::map);
    }

    @Override
    public void update(Account account) {
        AccountEntity accountEntity = accountRepository.findById(account.getId().getValue())
                .orElseThrow(() -> new AccountEntityNotFoundException("계좌 정보를 찾을 수 없습니다."));

        accountEntity.updateAccountHolderName(account.getAccountHolderName());
        accountEntity.updateBalance(account.getBalance().amount());
        accountEntity.changeStatus(account.getStatus());
        accountEntity.updateWithdrawLimitAmount(account.getWithdrawLimitAmount().amount());
        accountEntity.updateTransferLimitAmount(account.getTransferLimitAmount().amount());
        accountEntity.updateTransferFeeRate(account.getTransferFeeRate().percentage());
        accountEntity.updateDestroyedAt(account.getDestroyedAt());

        account.getTransactions().stream()
                .map(transaction -> {
                    AccountTransactionEntity transactionEntity = mapper.map(transaction);

                    if (transaction instanceof ReceiveTransaction) {
                        AccountEntity senderAccount = accountRepository.findById(((ReceiveTransaction) transaction).getSenderAccountId().getValue())
                                .orElseThrow(() -> new AccountEntityNotFoundException("송금 계좌 정보를 찾을 수 없습니다."));
                        ((ReceiveTransactionEntity) transactionEntity).assignSenderAccount(senderAccount);
                    } else if (transaction instanceof TransferTransaction) {
                        AccountEntity receiverAccount = accountRepository.findById(((TransferTransaction) transaction).getReceiverAccountId().getValue())
                                .orElseThrow(() -> new AccountEntityNotFoundException("수신 계좌 정보를 찾을 수 없습니다."));
                        ((TransferTransactionEntity) transactionEntity).assignReceiverAccount(receiverAccount);
                    }

                    return transactionEntity;
                })
                .filter(Objects::nonNull)
                .forEach(accountEntity::addTransaction);
    }

    @Override
    public Page<AccountTransactionRetrieveResponse.Transaction> findAllAccountTransaction(AccountId accountId, int page, int size) {
        return transactionRepository
                .findAllTransferOrReceiveTransactionByAccountId(accountId.getValue(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionAt")))
                .map(accountTransactionEntity -> {
                    if (accountTransactionEntity instanceof TransferTransactionEntity) {
                        return new AccountTransactionRetrieveResponse.Transaction(
                                AccountNumber.of(accountTransactionEntity.getAccount().getAccountNumber()),
                                AccountNumber.of(((TransferTransactionEntity) accountTransactionEntity).getReceiverAccount().getAccountNumber()),
                                Money.of(accountTransactionEntity.getAmount()),
                                accountTransactionEntity.getTransactionAt()
                        );
                    } else if (accountTransactionEntity instanceof ReceiveTransactionEntity) {
                        return new AccountTransactionRetrieveResponse.Transaction(
                                AccountNumber.of(((ReceiveTransactionEntity) accountTransactionEntity).getSenderAccount().getAccountNumber()),
                                AccountNumber.of(accountTransactionEntity.getAccount().getAccountNumber()),
                                Money.of(accountTransactionEntity.getAmount()),
                                accountTransactionEntity.getTransactionAt()
                        );
                    } else {
                        return null;
                    }
                });
    }
}
