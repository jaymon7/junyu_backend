package com.bank.adapter.output.persistence.exception;

public class AccountEntityNotFoundException extends PersistenceException {
    public AccountEntityNotFoundException(String message) {
        super(message);
    }

    public AccountEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
