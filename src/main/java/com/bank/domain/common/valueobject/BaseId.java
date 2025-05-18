package com.bank.domain.common.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class BaseId<T> {
    protected T value;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseId<?> baseId)) return false;
        return Objects.equals(value, baseId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
