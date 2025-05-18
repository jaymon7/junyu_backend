package com.bank.domain.account.valueobject;

import java.util.UUID;

public record AccountNumber(String value) {
    public static AccountNumber of(String value) {
        return new AccountNumber(value);
    }

   public static AccountNumber generateAccountNumber() {
       String part1 = String.format("%04d", Math.abs(UUID.randomUUID().getMostSignificantBits()) % 10000);
       String part2 = String.format("%03d", Math.abs(UUID.randomUUID().getLeastSignificantBits()) % 1000);
       String part3 = String.format("%06d", System.currentTimeMillis() % 1000000);
       String accountNumber = String.format("%s-%s-%s", part1, part2, part3);
       return new AccountNumber(accountNumber);
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return this.value.equals(that.value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
