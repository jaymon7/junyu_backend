package com.bank.domain.account.exception;

public abstract class AccountDomainException extends RuntimeException{
    protected AccountDomainException(String message) {
        super(message);
    }

    protected AccountDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
