package com.bank.domain.account.entity;

import com.bank.domain.account.exception.AccountStatusInvalidException;
import com.bank.domain.account.exception.InsufficientBalanceException;
import com.bank.domain.account.exception.InvalidTransferException;
import com.bank.domain.account.valueobject.AccountId;
import com.bank.domain.account.valueobject.AccountNumber;
import com.bank.domain.account.valueobject.FeeRate;
import com.bank.domain.account.valueobject.Money;
import com.bank.domain.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
public class Account extends BaseEntity<AccountId> {
    public static final Money DEFAULT_WITHDRAW_LIMIT_AMOUNT = Money.of(new BigDecimal(1_000_000));
    public static final Money DEFAULT_TRANSFER_LIMIT_AMOUNT = Money.of(new BigDecimal(3_000_000));
    public static final FeeRate DEFAULT_TRANSFER_FEE_RATE = FeeRate.of(new BigDecimal("0.01"));

    private AccountNumber accountNumber;
    private String accountHolderName;
    private Money balance;
    private AccountStatus status;
    private Money withdrawLimitAmount;
    private Money transferLimitAmount;
    private FeeRate transferFeeRate;
    private ZonedDateTime createdAt;
    private ZonedDateTime destroyedAt;
    private List<AccountTransaction> transactions;

    @Builder
    public Account(AccountId id, AccountNumber accountNumber, String accountHolderName, Money balance, AccountStatus status, Money withdrawLimitAmount, Money transferLimitAmount, FeeRate transferFeeRate, ZonedDateTime createdAt, ZonedDateTime destroyedAt, List<AccountTransaction> transactions) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = Optional.ofNullable(balance).map(Money::amount).map(Money::of).orElse(Money.ZERO);
        this.status = Optional.ofNullable(status).orElse(AccountStatus.ACTIVE);
        this.withdrawLimitAmount = Optional.ofNullable(withdrawLimitAmount).map(Money::amount).map(Money::of).orElse(new Money(DEFAULT_WITHDRAW_LIMIT_AMOUNT.amount()));
        this.transferLimitAmount = Optional.ofNullable(transferLimitAmount).map(Money::amount).map(Money::of).orElse(new Money(DEFAULT_TRANSFER_LIMIT_AMOUNT.amount()));
        this.transferFeeRate = Optional.ofNullable(transferFeeRate).map(FeeRate::percentage).map(FeeRate::of).orElse(new FeeRate(DEFAULT_TRANSFER_FEE_RATE.percentage()));
        this.createdAt = createdAt;
        this.destroyedAt = destroyedAt;
        this.transactions = Optional.ofNullable(transactions).map(ArrayList::new).orElse(new ArrayList<>());
    }

    public static Account createAccount(String accountHolderName) {
        return Account.builder()
                .id(AccountId.generateId())
                .accountNumber(AccountNumber.generateAccountNumber())
                .accountHolderName(accountHolderName)
                .balance(Money.ZERO)
                .status(AccountStatus.ACTIVE)
                .transactions(new ArrayList<>())
                .withdrawLimitAmount(Money.of(DEFAULT_WITHDRAW_LIMIT_AMOUNT.amount()))
                .transferLimitAmount(Money.of(DEFAULT_TRANSFER_LIMIT_AMOUNT.amount()))
                .transferFeeRate(FeeRate.of(DEFAULT_TRANSFER_FEE_RATE.percentage()))
                .createdAt(ZonedDateTime.now())
                .build();
    }

    /*
     * 계좌 파기
     */
    public void destroy() {
        if (this.status == AccountStatus.DESTROYED) {
            throw new AccountStatusInvalidException("계좌 파기 실패: 이미 파기된 계좌입니다.");
        }

        this.status = AccountStatus.DESTROYED;
        this.destroyedAt = ZonedDateTime.now();
    }
    /*
     * 예금
     */
    public DepositTransaction deposit(Money amount) {
        if (! status.canDeposit()) {
            throw new AccountStatusInvalidException("입금 실패: 계좌 상태가 출금을 허용하지 않습니다.");
        }

        this.balance = this.balance.add(amount);
        DepositTransaction transaction = DepositTransaction.recordDepositTransaction(
                getId(),
                amount,
                this.balance
        );

        this.transactions.add(transaction);

        return transaction;
    }
    /*
     * 출금
     */
    public WithdrawalTransaction withdraw(Money amount) {
        if (! status.canWithdraw()) {
            throw new AccountStatusInvalidException("출금 실패: 계좌 상태가 출금을 허용하지 않습니다.");
        } else if (amount.isGreaterThan(this.balance)) {
            throw new InsufficientBalanceException("출금 실패: 잔액이 부족합니다.");
        } else if (getTotalWithdrawalAmountToday().add(amount).isGreaterThan(withdrawLimitAmount)) {
            throw new InsufficientBalanceException("출금 실패: 출금 한도를 초과했습니다.");
        }

        this.balance = this.balance.subtract(amount);
        WithdrawalTransaction transaction = WithdrawalTransaction.recordWithdrawalTransaction(
                getId(),
                amount,
                this.balance
        );

        this.transactions.add(transaction);

        return transaction;
    }
    /*
     * 송금
     */
    public TransferTransaction transfer(AccountId receiverAccountId, Money amount) {
        if (! status.canTransfer()) {
            throw new AccountStatusInvalidException("이체 실패: 계좌 상태가 출금을 허용하지 않습니다.");
        } else if (this.id.equals(receiverAccountId)) {
            throw new InvalidTransferException("이체 실패: 본인 계좌로 이체할 수 없습니다.");
        } else if (applyTransferFee(amount).add(amount).isGreaterThan(this.balance)) {
            throw new InsufficientBalanceException("이체 실패: 잔액이 부족합니다.");
        } else if (getTotalTransferAmountToday().add(amount).isGreaterThan(transferLimitAmount)) {
            throw new InsufficientBalanceException("이체 실패: 이체 한도를 초과했습니다.");
        }

        // 수수료 적용
        Money transferFee = applyTransferFee(amount);
        this.balance = this.balance.subtract(amount.add(transferFee));

        TransferTransaction transaction = TransferTransaction.recordTransferTransaction(
                getId(),
                receiverAccountId,
                transferFee,
                amount,
                this.balance
        );

        this.transactions.add(transaction);

        return transaction;
    }
    /*
     * 수취
     */
    public void receive(AccountId senderAccountId, Money amount) {
        if (! status.canTransfer()) {
            throw new AccountStatusInvalidException("수취 실패: 계좌 상태가 수취를 허용하지 않습니다.");
        }

        this.balance = this.balance.add(amount);
        ReceiveTransaction transaction = ReceiveTransaction.recordReceiveTransaction(
                senderAccountId,
                getId(),
                amount,
                this.balance
        );

        this.transactions.add(transaction);
    }

    public void addTransaction(AccountTransaction transaction) {
        this.transactions.add(transaction);
    }

    private Money getTotalWithdrawalAmountToday() {
        return transactions.stream()
                .filter(transaction -> transaction instanceof WithdrawalTransaction)
                .filter(transaction -> transaction.getTransactionAt() != null)
                .filter(transaction -> transaction.getTransactionAt().toLocalDate().equals(ZonedDateTime.now().toLocalDate()))
                .map(AccountTransaction::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private Money getTotalTransferAmountToday() {
        return transactions.stream()
                .filter(transaction -> transaction instanceof TransferTransaction)
                .filter(transaction -> transaction.getTransactionAt() != null)
                .filter(transaction -> transaction.getTransactionAt().toLocalDate().equals(ZonedDateTime.now().toLocalDate()))
                .map(AccountTransaction::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private Money applyTransferFee(Money amount) {
        return transferFeeRate.calculateFee(amount);
    }
}
