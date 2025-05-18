package com.bank.domain.account.exception;

public class AccountStatusInvalidException extends AccountDomainException {
    public AccountStatusInvalidException(String message) {
        super(message);
    }

    public AccountStatusInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
