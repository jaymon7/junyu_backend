package com.bank.domain.account.exception;

public class InvalidTransferException extends AccountDomainException {
    public InvalidTransferException(String message) {
        super(message);
    }

    public InvalidTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
