package com.bank.adapter.output.persistence;

import com.bank.adapter.output.persistence.entity.AccountEntity;
import com.bank.adapter.output.persistence.mapper.AccountEntityMapper;
import com.bank.adapter.output.persistence.repository.AccountJpaRepository;
import com.bank.domain.account.entity.Account;
import com.bank.domain.account.entity.WithdrawalTransaction;
import com.bank.domain.account.valueobject.FeeRate;
import com.bank.domain.account.valueobject.Money;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class PersistenceAccountRepositoryServiceTest {

    @SpyBean
    private PersistenceAccountRepositoryService persistenceAccountRepositoryService;

    @SpyBean
    private AccountEntityMapper mapper;

    @SpyBean
    private AccountJpaRepository accountJpaRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("계좌 생성 성공")
    void 계좌_생성_성공() {
        // given
        Account account = Account.createAccount("홍길동");

        // when
        persistenceAccountRepositoryService.create(account);
        // DB 에 반영
        em.flush();

        // then
        AccountEntity accountEntity = accountJpaRepository.findById(account.getId().getValue())
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다."));

        assertEquals(account.getAccountNumber().value(), accountEntity.getAccountNumber(), "계좌 번호가 일치하지 않습니다.");
        assertEquals(account.getAccountHolderName(), accountEntity.getAccountHolderName(), "계좌 소유자 이름이 일치하지 않습니다.");
        assertEquals(account.getBalance().amount(), accountEntity.getBalance(), "계좌 잔액이 일치하지 않습니다.");
        assertEquals(account.getStatus(), accountEntity.getStatus(), "계좌 상태가 일치하지 않습니다.");
        assertEquals(account.getWithdrawLimitAmount().amount(), accountEntity.getWithdrawLimitAmount(), "계좌 출금 한도가 일치하지 않습니다.");
        assertEquals(account.getTransferLimitAmount().amount(), accountEntity.getTransferLimitAmount(), "계좌 이체 한도가 일치하지 않습니다.");
        assertEquals(account.getTransferFeeRate().percentage(), accountEntity.getTransferFeeRate(), "계좌 이체 수수료 비율이 일치하지 않습니다.");
        assertEquals(account.getCreatedAt(), accountEntity.getCreatedAt(), "계좌 생성 시간이 일치하지 않습니다.");
        assertEquals(account.getDestroyedAt(), accountEntity.getDestroyedAt(), "계좌 파기 시간이 일치하지 않습니다.");
        assertEquals(account.getTransactions().size(), accountEntity.getTransactions().size(), "계좌 거래 내역 수가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("계좌 조회 성공")
    void 계좌_조회_성공() {
        // given
        Account account = Account.createAccount("홍길동");
        accountJpaRepository.save(mapper.map(account));
        // DB 에 반영
        em.flush();

        // when
        Account findAccount = persistenceAccountRepositoryService.findByAccountNumber(account.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다."));

        // then
        assertEquals(account.getAccountNumber().value(), findAccount.getAccountNumber().value(), "계좌 번호가 일치하지 않습니다.");
        assertEquals(account.getAccountHolderName(), findAccount.getAccountHolderName(), "계좌 소유자 이름이 일치하지 않습니다.");
        assertEquals(account.getBalance().amount(), findAccount.getBalance().amount(), "계좌 잔액이 일치하지 않습니다.");
        assertEquals(account.getStatus(), findAccount.getStatus(), "계좌 상태가 일치하지 않습니다.");
        assertEquals(account.getWithdrawLimitAmount().amount(), findAccount.getWithdrawLimitAmount().amount(), "계좌 출금 한도가 일치하지 않습니다.");
        assertEquals(account.getTransferLimitAmount().amount(), findAccount.getTransferLimitAmount().amount(), "계좌 이체 한도가 일치하지 않습니다.");
        assertEquals(account.getTransferFeeRate().percentage(), findAccount.getTransferFeeRate().percentage(), "계좌 이체 수수료 비율이 일치하지 않습니다.");
        assertEquals(account.getCreatedAt(), findAccount.getCreatedAt(), "계좌 생성 시간이 일치하지 않습니다.");
        assertEquals(account.getDestroyedAt(), findAccount.getDestroyedAt(), "계좌 파기 시간이 일치하지 않습니다.");
        assertEquals(account.getTransactions().size(), findAccount.getTransactions().size(), "계좌 거래 내역 수가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("계좌 업데이트 성공")
    void 계좌_업데이트_성공() {
        // given
        Account account = Account.createAccount("홍길동");
        accountJpaRepository.save(mapper.map(account));
        // DB 에 반영
        em.flush();
        Account updateAccount = Account.builder()
                .id(account.getId())
                .balance(Money.of(new BigDecimal(1_000_000)))
                .accountNumber(account.getAccountNumber())
                .accountHolderName("홍길동 개명")
                .status(account.getStatus())
                .withdrawLimitAmount(Money.of(new BigDecimal(2_000_000)))
                .transferLimitAmount(Money.of(new BigDecimal(5_000_000)))
                .transferFeeRate(FeeRate.of(new BigDecimal("0.05")))
                .transactions(
                        List.of(
                                WithdrawalTransaction.recordWithdrawalTransaction(
                                        account.getId(),
                                        Money.of(new BigDecimal(1_000_000)),
                                        account.getBalance().subtract(Money.of(new BigDecimal(1_000_000)))
                                )
                        )
                )
                .createdAt(account.getCreatedAt())
                .build();

        // when
        persistenceAccountRepositoryService.update(updateAccount);
        em.flush();

        // then
        AccountEntity accountEntity = accountJpaRepository.findByAccountNumber(account.getAccountNumber().value())
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다."));

        assertEquals(updateAccount.getAccountNumber().value(), accountEntity.getAccountNumber(), "계좌 번호가 일치하지 않습니다.");
        assertEquals(updateAccount.getAccountHolderName(), accountEntity.getAccountHolderName(), "계좌 소유자 이름이 일치하지 않습니다.");
        assertEquals(updateAccount.getBalance().amount(), accountEntity.getBalance(), "계좌 잔액이 일치하지 않습니다.");
        assertEquals(updateAccount.getStatus(), accountEntity.getStatus(), "계좌 상태가 일치하지 않습니다.");
        assertEquals(updateAccount.getWithdrawLimitAmount().amount(), accountEntity.getWithdrawLimitAmount(), "계좌 출금 한도가 일치하지 않습니다.");
        assertEquals(updateAccount.getTransferLimitAmount().amount(), accountEntity.getTransferLimitAmount(), "계좌 이체 한도가 일치하지 않습니다.");
        assertEquals(updateAccount.getTransferFeeRate().percentage(), accountEntity.getTransferFeeRate(), "계좌 이체 수수료 비율이 일치하지 않습니다.");
        assertEquals(updateAccount.getCreatedAt(), accountEntity.getCreatedAt(), "계좌 생성 시간이 일치하지 않습니다.");
        assertEquals(updateAccount.getDestroyedAt(), accountEntity.getDestroyedAt(), "계좌 파기 시간이 일치하지 않습니다.");
        assertEquals(updateAccount.getTransactions().size(), accountEntity.getTransactions().size(), "계좌 거래 내역 수가 일치하지 않습니다.");
    }
}