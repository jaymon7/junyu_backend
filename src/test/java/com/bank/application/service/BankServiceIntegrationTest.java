package com.bank.application.service;

import com.bank.adapter.output.persistence.entity.AccountEntity;
import com.bank.adapter.output.persistence.repository.AccountJpaRepository;
import com.bank.application.port.input.dto.*;
import com.bank.application.port.output.persistence.AccountRepository;
import com.bank.domain.account.entity.Account;
import com.bank.domain.account.entity.AccountStatus;
import com.bank.domain.account.exception.AccountStatusInvalidException;
import com.bank.domain.account.exception.InsufficientBalanceException;
import com.bank.domain.account.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ImportAutoConfiguration(exclude = { SwaggerConfig.class })
class BankServiceIntegrationTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Test
    @DisplayName("계좌 생성 성공")
    void 계좌_생성_성공() {
        // given
        CreateAccountCommand accountCommand = new CreateAccountCommand("홍길동");

        // when
        CreateAccountResponse accountResponse = bankService.createAccount(accountCommand);

        // then
        Optional<AccountEntity> findAccount = accountJpaRepository.findById(accountResponse.accountId().getValue());

        assertEquals(true, findAccount.isPresent(), "계좌가 정상적으로 생성되어야 합니다.");
        assertEquals("홍길동", accountResponse.accountHolderName(), "계좌 소유자 이름이 일치해야 합니다.");
        assertEquals(0, accountResponse.balance().amount().intValue(), "계좌 잔액이 0이어야 합니다.");
    }

    @Test
    @DisplayName("계좌 삭제 성공 - 동시 요청시 계좌 번호 중복되는지 확인(계좌 번호는 유니크 설정 되어 있음)")
    void 계좌_삭제_성공_동시요청() {
        // given
        int executorCount = 1000;
        CompletableFuture[] futures = new CompletableFuture[executorCount];

        for (int i = 0; i < executorCount; i++) {
            CreateAccountCommand accountCommand = new CreateAccountCommand("계좌 동시성");

            futures[i] = CompletableFuture.supplyAsync(() -> {
                System.out.println("계좌 생성 중...[" + Thread.currentThread().getName() + "]");
                return bankService.createAccount(accountCommand);
            });
        }

        // when
        CompletableFuture.allOf(futures).join();

        // then
        List<AccountEntity> accountEntities = accountJpaRepository.findAll()
                .stream()
                .filter(accountEntity -> accountEntity.getAccountHolderName().equals("계좌 동시성"))
                .toList();
        assertEquals(executorCount, accountEntities.size(), "계좌가 정상적으로 삭제되어야 합니다.");
    }

    @Test
    @DisplayName("계좌 삭제 성공")
    void 계좌_삭제_성공() {
        // given
        Account account = Account.createAccount("홍길동");
        accountRepository.create(account);

        DestroyAccountCommand destroyAccountCommand = new DestroyAccountCommand(account.getAccountNumber());
        // when
        bankService.destroyAccount(destroyAccountCommand);

        // then
        Optional<AccountEntity> findAccount = accountJpaRepository.findById(account.getId().getValue());
        assertEquals(true, findAccount.isPresent(), "계좌 정보가 존재해야 합니다. (삭제되지 않아야 합니다.)");
        assertEquals(AccountStatus.DESTROYED, findAccount.get().getStatus(), "계좌 상태가 DESTROYED여야 합니다.");
    }

    @Test
    @DisplayName("계좌 삭제 - 동시 요청시 첫 요청 제외 모든 요청은 실패해야한다.(계좌 조회시 디비락 걸림)")
    void 계좌_삭제_동시요청() {
        // given
        Account account = Account.createAccount("홍길동");
        accountRepository.create(account);
        int executorCount = 1000;
        CompletableFuture[] futures = new CompletableFuture[executorCount];
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < executorCount; i++) {
            DestroyAccountCommand destroyAccountCommand = new DestroyAccountCommand(account.getAccountNumber());

            futures[i] = CompletableFuture.runAsync(() -> {
                System.out.println("계좌 삭제 중...[" + Thread.currentThread().getName() + "]");
                try {
                    bankService.destroyAccount(destroyAccountCommand);
                } catch (RuntimeException e) {
                    System.out.println("계좌 삭제 실패: " + e.getMessage());
                    failCount.incrementAndGet();
                }
            });
        }

        // when
        CompletableFuture.allOf(futures).join();

        // then
        Optional<AccountEntity> findAccount = accountJpaRepository.findById(account.getId().getValue());

        assertEquals(true, findAccount.isPresent(), "계좌 정보가 존재해야 합니다. (삭제되지 않아야 합니다.)");
        assertEquals(AccountStatus.DESTROYED, findAccount.get().getStatus(), "계좌 상태가 DESTROYED여야 합니다.");
        assertEquals(executorCount - 1, failCount.get(), "한개의 요청을 제외한 모든 요청은 실패해야 합니다.");
    }

    @Test
    @DisplayName("계좌 입금 성공")
    void 계좌_입금_성공() {
        // given
        Account account = Account.createAccount("홍길동");
        Money amount = Money.of(new BigDecimal(2_000_000L));;
        accountRepository.create(account);

        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(account.getAccountNumber(), amount);

        // when
        DepositMoneyResponse depositMoneyResponse = bankService.deposit(depositMoneyCommand);

        // then
        assertEquals(amount.amount().intValue(), depositMoneyResponse.balance().amount().intValue(), "계좌 잔액이 2,000,000이어야 합니다.");
    }

    @Test
    @DisplayName("계좌 입금 성공 - 동시 요청시 계좌 잔액이 정확해야 한다.")
    void 계좌_입금_성공_동시요청() {
        // given
        CreateAccountResponse accountResponse = bankService.createAccount(new CreateAccountCommand("홍길동"));

        int executorCount = 1000;
        CompletableFuture[] futures = new CompletableFuture[executorCount];
        AtomicLong totalAmount = new AtomicLong(0);

        for (int i = 0; i < executorCount; i++) {
            Money amount = Money.of(new BigDecimal(new Random().nextLong(1_000_000L)));
            totalAmount.updateAndGet(v -> v + amount.amount().longValue());
            DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(accountResponse.accountNumber(), amount);

            futures[i] = CompletableFuture.supplyAsync(() -> bankService.deposit(depositMoneyCommand));
        }

        // when
        CompletableFuture.allOf(futures).join();

        // then
        Optional<AccountEntity> findAccount = accountJpaRepository.findById(accountResponse.accountId().getValue());

        assertEquals(true, findAccount.isPresent(), "계좌 정보가 존재해야 합니다.");
        assertEquals(totalAmount.get(), findAccount.get().getBalance().longValue(), "계좌 잔액이 정확해야 합니다.");
    }

    @Test
    @DisplayName("계좌 출금 성공")
    void 계좌_출금_성공() {
        // given
        Account account = Account.createAccount("홍길동");
        Money depositAmount = Money.of(new BigDecimal(2_000_000L));
        accountRepository.create(account);
        bankService.deposit(new DepositMoneyCommand(account.getAccountNumber(), depositAmount));

        Money withdrawAmount = Money.of(new BigDecimal(1_000_000L));
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(account.getAccountNumber(), withdrawAmount);

        // when
        WithdrawMoneyResponse withdrawMoneyResponse = bankService.withdraw(withdrawMoneyCommand);

        // then
        assertEquals(withdrawAmount.amount().intValue(), withdrawMoneyResponse.balance().amount().intValue(), "계좌 잔액이 1,000,000이어야 합니다.");
    }

    @Test
    @DisplayName("계좌 출금 성공 - 동시 요청시 계좌 잔액이 정확해야 한다.")
    void 계좌_출금_성공_동시요청() {
        // given
        CreateAccountResponse accountResponse = bankService.createAccount(new CreateAccountCommand("홍길동"));
        Money depositAmount = Money.of(new BigDecimal(2_000_000L));
        bankService.deposit(new DepositMoneyCommand(accountResponse.accountNumber(), depositAmount));

        int executorCount = 1000;
        CompletableFuture[] futures = new CompletableFuture[executorCount];
        AtomicLong totalAmount = new AtomicLong(0);

        for (int i = 0; i < executorCount; i++) {
            Money withdrawAmount = Money.of(new BigDecimal(new Random().nextLong(1_000)));
            totalAmount.updateAndGet(v -> v + withdrawAmount.amount().longValue());
            WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(accountResponse.accountNumber(), withdrawAmount);

            futures[i] = CompletableFuture.supplyAsync(() -> bankService.withdraw(withdrawMoneyCommand));
        }

        // when
        CompletableFuture.allOf(futures).join();

        // then
        AccountEntity findAccount = accountJpaRepository.findById(accountResponse.accountId().getValue())
                        .orElseThrow();

        assertEquals(depositAmount.amount().longValue() - totalAmount.get(), findAccount.getBalance().longValue(), "계좌 잔액이 정확해야 합니다.");
    }

    @Test
    @DisplayName("계좌 출금 실패 - 잔액 부족")
    void 계좌_출금_실패_잔액부족() {
        // given
        Account account = Account.createAccount("홍길동");
        Money depositAmount = Money.of(new BigDecimal(2_000_000L));
        accountRepository.create(account);
        bankService.deposit(new DepositMoneyCommand(account.getAccountNumber(), depositAmount));

        Money withdrawAmount = Money.of(new BigDecimal(3_000_000L));
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(account.getAccountNumber(), withdrawAmount);

        // when
        try {
            bankService.withdraw(withdrawMoneyCommand);
            fail("계좌 잔액 부족 예외가 발생해야 합니다.");
        } catch (RuntimeException e) {
            assertInstanceOf(InsufficientBalanceException.class, e, "계좌 잔액 부족 예외가 발생해야 합니다.");
        }
    }

    @Test
    @DisplayName("계좌 출금 실패 - 계좌 상태 비정상")
    void 계좌_출금_실패_계좌상태비정상() {
        // given
        Account account = Account.createAccount("홍길동");
        Money depositAmount = Money.of(new BigDecimal(2_000_000L));
        accountRepository.create(account);
        bankService.deposit(new DepositMoneyCommand(account.getAccountNumber(), depositAmount));

        DestroyAccountCommand destroyAccountCommand = new DestroyAccountCommand(account.getAccountNumber());
        bankService.destroyAccount(destroyAccountCommand);

        Money withdrawAmount = Money.of(new BigDecimal(1_000_000L));
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(account.getAccountNumber(), withdrawAmount);

        // when
        try {
            bankService.withdraw(withdrawMoneyCommand);
            fail("계좌 상태 비정상 예외가 발생해야 합니다.");
        } catch (RuntimeException e) {
            assertInstanceOf(AccountStatusInvalidException.class, e, "계좌 상태 비정상 예외가 발생해야 합니다.");
        }
    }

    @Test
    @DisplayName("계좌 송금 성공")
    void 계좌_송금_성공() {
        // given
        Account senderAccount = Account.createAccount("홍길동");
        Account receiverAccount = Account.createAccount("김철수");
        Money depositAmount = Money.of(new BigDecimal(2_000_000L));
        accountRepository.create(senderAccount);
        accountRepository.create(receiverAccount);
        bankService.deposit(new DepositMoneyCommand(senderAccount.getAccountNumber(), depositAmount));

        Money transferAmount = Money.of(new BigDecimal(1_000_000L));
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(senderAccount.getAccountNumber(), receiverAccount.getAccountNumber(), transferAmount);

        // when
        TransferMoneyResponse transferMoneyResponse = bankService.transfer(transferMoneyCommand);

        // then
        assertEquals(depositAmount.subtract(transferAmount).subtract(transferMoneyResponse.transferFee()).amount(), transferMoneyResponse.balance().amount(), "송금자 계좌 잔액이 정확해야 합니다.(수수료 계산)");
    }

    @Test
    @DisplayName("계좌 송금/수취 조회 성공")
    void 계좌_송금_수취_조회_성공() {
        // given
        CreateAccountResponse senderAccountResponse = bankService.createAccount(new CreateAccountCommand("홍길동"));
        CreateAccountResponse receiverAccountResponse = bankService.createAccount(new CreateAccountCommand("김철수"));
        bankService.deposit(
                new DepositMoneyCommand(senderAccountResponse.accountNumber(), Money.of(new BigDecimal(2_000_000L)))
        );
        bankService.transfer(
                new TransferMoneyCommand(senderAccountResponse.accountNumber(), receiverAccountResponse.accountNumber(), Money.of(new BigDecimal(1_000_000L)))
        );

        // when
        AccountTransactionRetrieveResponse retrieveResponse = bankService.retrieveAccountTransactions(
                new AccountTransactionRetrieveCommand(senderAccountResponse.accountNumber(), 0, 10)
        );

        // then
        assertEquals(1, retrieveResponse.accountTransactionPage().getTotalElements(), "송금/수취 내역이 1건이어야 합니다.");
    }
}