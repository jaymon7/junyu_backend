package com.bank.domain.account.entity;

import com.bank.domain.account.exception.AccountDomainException;
import com.bank.domain.account.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class AccountTest {

    @Test
    @DisplayName("계좌 생성 성공")
    void 계좌_생성_성공() {
        // given
        // when
        Account createAccount = Account.createAccount("홍길동");

        // then
        assertEquals("홍길동", createAccount.getAccountHolderName(), "계좌 생성 후 계좌 소유자 이름이 일치해야 한다.");
        assertNotNull(createAccount.getAccountNumber(), "계좌 생성 후 계좌 번호는 null이 아니어야 한다.");
        assertNotNull(createAccount.getId(), "생성된 계좌의 ID는 null이 아니어야 한다.");
        assertEquals(Money.ZERO, createAccount.getBalance(), "생성된 계좌의 잔액은 0이어야 한다.");
        assertEquals(AccountStatus.ACTIVE, createAccount.getStatus(), "생성된 계좌의 상태는 ACTIVE 상태이여야 한다.");
        assertEquals(Account.DEFAULT_WITHDRAW_LIMIT_AMOUNT, createAccount.getWithdrawLimitAmount(), "생성된 계좌의 출금 한도는 기본 출금 한도와 같아야 한다.");
        assertEquals(Account.DEFAULT_TRANSFER_LIMIT_AMOUNT, createAccount.getTransferLimitAmount(), "생성된 계좌의 이체 한도는 기본 이체 한도와 같아야 한다.");
        assertEquals(Account.DEFAULT_TRANSFER_FEE_RATE, createAccount.getTransferFeeRate(), "생성된 계좌의 이체 수수료는 기본 이체 수수료와 같아야 한다.");
        assertNotNull(createAccount.getCreatedAt(), "생성된 계좌의 생성 시간은 null이 아니어야 한다.");
        assertNull(createAccount.getDestroyedAt(), "생성된 계좌의 파기 시간은 null이어야 한다.");
        assertTrue(createAccount.getTransactions().isEmpty(), "생성된 계좌의 거래 내역은 비어 있어야 한다.");
    }

    @Test
    @DisplayName("계좌 파기 성공")
    void 계좌_파기_성공() {
        // given
        Account account = Account.createAccount("홍길동");

        //when
        account.destroy();

        // then
        assertEquals(AccountStatus.DESTROYED, account.getStatus(), "계좌 파기 후 상태는 DESTROYED여야 한다.");
        assertNotNull(account.getDestroyedAt(), "계좌 파기 후 파기 시간은 null이 아니어야 한다.");
    }

    @Test
    @DisplayName("계좌 파기 실패(이미 파기된 계좌)")
    void 계좌_파기_실패1() {
        // given
        Account account = Account.builder()
                .status(AccountStatus.DESTROYED)
                .destroyedAt(ZonedDateTime.now())
                .build();

        // when
        try {
            account.destroy();
            fail("계좌 파기 실패 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("계좌 파기 실패: 이미 파기된 계좌입니다.", e.getMessage(), "계좌 파기 실패 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("예금 성공")
    void 예금_성공() {
        // given
        Money balance = Money.of(BigDecimal.valueOf(new Random().nextLong(1_000_000_000_000L)));
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(Money.of(balance.amount()))
                .build();
        Money depositAmount = Money.of(BigDecimal.valueOf(new Random().nextLong(1_000_000_000_000L)));

        // when
        account.deposit(depositAmount);

        // then
        assertEquals(account.getBalance(), balance.add(depositAmount), "예금 후 잔액이 일치하지 않습니다.");
    }

    @Test
    @DisplayName("예금 실패(파기된 계좌)")
    void 예금_실패1() {
        // given
        Account account = Account.builder()
                .status(AccountStatus.DESTROYED)
                .destroyedAt(ZonedDateTime.now())
                .build();
        Money depositAmount = Money.of(BigDecimal.valueOf(new Random().nextLong(1_000_000_000_000L)));

        // when
        try {
            account.deposit(depositAmount);
            fail("계좌 파기 실패 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("입금 실패: 계좌 상태가 출금을 허용하지 않습니다.", e.getMessage(), "입금 실패 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("출금 성공")
    void 출금_성공() {
        // given
        Money balance = Money.of(BigDecimal.valueOf(1_000));
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(Money.of(balance.amount()))
                .transactions(
                        List.of(
                                WithdrawalTransaction.builder()
                                        .amount(Money.of(BigDecimal.valueOf(999_000)))
                                        .transactionAt(ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0))
                                        .build()
                        )
                )
                .build();
        Money withdrawAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        account.withdraw(withdrawAmount);

        // then
        assertEquals(account.getBalance(), balance.subtract(withdrawAmount), "출금 후 잔액이 일치하지 않습니다.");
    }

    @Test
    @DisplayName("출금 실패(파기된 계좌)")
    void 출금_실패1() {
        // given
        Money balance = Money.of(BigDecimal.valueOf(1_000));
        Account account = Account.builder()
                .status(AccountStatus.DESTROYED)
                .balance(balance)
                .destroyedAt(ZonedDateTime.now())
                .build();
        Money withdrawAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        try {
            account.withdraw(withdrawAmount);
            fail("계좌 파기 실패 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("출금 실패: 계좌 상태가 출금을 허용하지 않습니다.", e.getMessage(), "출금 실패 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("출금 실패(잔액 부족)")
    void 출금_실패2() {
        // given
        Money balance = Money.of(BigDecimal.valueOf(1_000));
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(balance)
                .build();
        Money withdrawAmount = Money.of(BigDecimal.valueOf(2_000));

        // when
        try {
            account.withdraw(withdrawAmount);
            fail("잔액 부족 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("출금 실패: 잔액이 부족합니다.", e.getMessage(), "잔액 부족 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("출금 실패(출금 한도 초과)")
    void 출금_실패3() {
        // given
        Money balance = Money.of(BigDecimal.valueOf(1_000));
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(balance)
                .transactions(
                        List.of(
                                WithdrawalTransaction.builder()
                                        .amount(Money.of(BigDecimal.valueOf(1_000_000)))
                                        .transactionAt(ZonedDateTime.now())
                                        .build()
                        )
                )
                .build();
        Money withdrawAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        try {
            account.withdraw(withdrawAmount);
            fail("출금 한도 초과 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("출금 실패: 출금 한도를 초과했습니다.", e.getMessage(), "출금 한도 초과 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("이체 성공")
    void 이체_성공() {
        // given
        Money balance = Money.of(BigDecimal.valueOf(2_000));
        Account transferAccount = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(Money.of(balance.amount()))
                .transactions(
                        List.of(
                                TransferTransaction.builder()
                                        .amount(Money.of(BigDecimal.valueOf(2_999_000)))
                                        .transactionAt(ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0))
                                        .build()
                        )
                )
                .build();
        Account receiveAccount = Account.createAccount("홍길동");

        Money transferAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        transferAccount.transfer(receiveAccount.getId(), transferAmount);

        // then
        assertEquals(transferAccount.getBalance(), balance.subtract(transferAmount.add(transferAccount.getTransferFeeRate().calculateFee(transferAmount))), "이체 후 잔액이 일치하지 않습니다.");
        assertEquals(2, transferAccount.getTransactions().size(), "이체 후 거래 내역이 추가되어야 한다.");
    }

    @Test
    @DisplayName("이체 실패(파기된 계좌)")
    void 이체_실패1() {
        // given
        Account transferAccount = Account.builder()
                .status(AccountStatus.DESTROYED)
                .destroyedAt(ZonedDateTime.now())
                .build();
        Account receiveAccount = Account.createAccount("홍길동");
        Money transferAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        try {
            transferAccount.transfer(receiveAccount.getId(), transferAmount);
            fail("계좌 파기 실패 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("이체 실패: 계좌 상태가 출금을 허용하지 않습니다.", e.getMessage(), "이체 실패 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("이체 실패(잔액 부족)")
    void 이체_실패2() {
        // given
        Account transferAccount = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(Money.of(BigDecimal.valueOf(1_000)))
                .build();
        Account receiveAccount = Account.createAccount("홍길동");
        Money transferAmount = Money.of(BigDecimal.valueOf(2_000));

        // when
        try {
            transferAccount.transfer(receiveAccount.getId(), transferAmount);
            fail("잔액 부족 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("이체 실패: 잔액이 부족합니다.", e.getMessage(), "잔액 부족 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("이체 실패(이체 한도 초과)")
    void 이체_실패3() {
        // given
        Account transferAccount = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(Money.of(BigDecimal.valueOf(2_000)))
                .transactions(
                        List.of(
                                TransferTransaction.builder()
                                        .amount(Money.of(BigDecimal.valueOf(3_000_000)))
                                        .transactionAt(ZonedDateTime.now())
                                        .build()
                        )
                )
                .build();
        Account receiveAccount = Account.createAccount("홍길동");
        Money transferAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        try {
            transferAccount.transfer(receiveAccount.getId(), transferAmount);
            fail("이체 한도 초과 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("이체 실패: 이체 한도를 초과했습니다.", e.getMessage(), "이체 한도 초과 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("이체 실패(수수료 초과)")
    void 이체_실패4() {
        // given
        Account transferAccount = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(Money.of(BigDecimal.valueOf(1_000)))
                .transactions(
                        List.of(
                                TransferTransaction.builder()
                                        .amount(Money.of(BigDecimal.valueOf(2_999_000)))
                                        .transactionAt(ZonedDateTime.now())
                                        .build()
                        )
                )
                .build();
        Account receiveAccount = Account.createAccount("홍길동");
        Money transferAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        try {
            transferAccount.transfer(receiveAccount.getId(), transferAmount);
            fail("이체 수수료 초과 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("이체 실패: 잔액이 부족합니다.", e.getMessage(), "이체 수수료 초과 예외 메시지가 일치해야 한다.");
        }
    }

    @Test
    @DisplayName("수신 성공")
    void 수신_성공() {
        // given
        Account transferAccount = Account.createAccount("홍길동");
        Account receiveAccount = Account.builder()
                .status(AccountStatus.ACTIVE)
                .balance(Money.of(BigDecimal.valueOf(1_000)))
                .build();
        Money transferAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        receiveAccount.receive(transferAccount.getId(), transferAmount);

        // then
        assertEquals(Money.of(BigDecimal.valueOf(2_000)), receiveAccount.getBalance(), "수신 후 잔액이 일치하지 않습니다.");
    }

    @Test
    @DisplayName("수신 실패(파기된 계좌)")
    void 수신_실패1() {
        // given
        Account transferAccount = Account.createAccount("홍길동");
        Account receiveAccount = Account.builder()
                .status(AccountStatus.DESTROYED)
                .destroyedAt(ZonedDateTime.now())
                .build();
        Money transferAmount = Money.of(BigDecimal.valueOf(1_000));

        // when
        try {
            receiveAccount.receive(transferAccount.getId(), transferAmount);
            fail("계좌 파기 실패 예외가 발생해야 한다.");
        } catch (AccountDomainException e) {
            // then
            assertEquals("수취 실패: 계좌 상태가 수취를 허용하지 않습니다.", e.getMessage(), "수신 실패 예외 메시지가 일치해야 한다.");
        }
    }
}