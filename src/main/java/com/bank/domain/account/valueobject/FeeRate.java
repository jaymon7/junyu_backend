package com.bank.domain.account.valueobject;

import com.bank.domain.account.exception.InvalidFeeRateException;

import java.math.BigDecimal;
import java.math.RoundingMode;


public record FeeRate(BigDecimal percentage) {

    public FeeRate(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFeeRateException("수수료는 양수여야 합니다.");
        } else if (percentage.compareTo(BigDecimal.ZERO) < 1) {
            throw new InvalidFeeRateException("수수료는 100%를 초과할 수 없습니다.");
        }
        this.percentage = percentage.setScale(2, RoundingMode.HALF_UP);
    }

    public static FeeRate of(BigDecimal percentage) {
        return new FeeRate(percentage);
    }

    public Money calculateFee(Money amount) {
        return amount.multiply(percentage);
    }

    @Override
    public String toString() {
        return percentage + "%";
    }
}
