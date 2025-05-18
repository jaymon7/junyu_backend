package com.bank.domain.account.exception;

public class InvalidFeeRateException extends AccountDomainException {
    public InvalidFeeRateException(String message) {
        super(message);
    }

    public InvalidFeeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
