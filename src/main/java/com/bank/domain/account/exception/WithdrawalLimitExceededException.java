package com.bank.domain.account.exception;

public class WithdrawalLimitExceededException extends AccountDomainException {
    public WithdrawalLimitExceededException(String message) {
        super(message);
    }

    public WithdrawalLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
