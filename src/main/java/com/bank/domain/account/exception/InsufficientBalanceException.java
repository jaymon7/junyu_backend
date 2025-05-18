package com.bank.domain.account.exception;

public class InsufficientBalanceException extends AccountDomainException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
