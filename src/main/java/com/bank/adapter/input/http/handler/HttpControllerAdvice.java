package com.bank.adapter.input.http.handler;

import com.bank.application.exception.NotFoundException;
import com.bank.domain.account.exception.AccountDomainException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class HttpControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(HttpControllerAdvice.class);

    public record ErrorResponse(int code, String message) {
    }

    public static ResponseEntity<ErrorResponse> toErrorResponse(HttpStatus status, Throwable throwable) {
        return toErrorResponse(status, throwable.getMessage());
    }

    public static ResponseEntity<ErrorResponse> toErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            BindException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception e) {
        logger.error("Bad request error", e);
        if (e instanceof MethodArgumentNotValidException) {
            String errorMessage = ((MethodArgumentNotValidException) e).getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .findFirst()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .orElse("Validation error");
            return toErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        }

        return toErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({
            NotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        logger.error("Not found error: {}", e.getMessage());

        return toErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({
            AccountDomainException.class
    })
    public ResponseEntity<ErrorResponse> handleAccountDomainException(AccountDomainException e) {
        logger.error("Account domain error: {}", e.getMessage());

        return toErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        logger.error("Internal server error: {}", e.getMessage());

        return toErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
}
