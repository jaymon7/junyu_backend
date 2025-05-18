package com.bank.domain.account.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {
    public static final Money ZERO = Money.of(BigDecimal.ZERO);

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public boolean isGreaterThanZero() {
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money) {
        return this.amount != null && this.amount.compareTo(money.amount()) > 0;
    }

    public Money add(Money money) {
        return new Money(setScale(this.amount.add(money.amount())));
    }

    public Money subtract(Money money) {
        return new Money(setScale(this.amount.subtract(money.amount())));
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(setScale(this.amount.multiply(multiplier)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return this.amount.compareTo(money.amount) == 0;
    }

    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
}
