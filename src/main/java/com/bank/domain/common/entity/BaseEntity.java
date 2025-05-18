package com.bank.domain.common.entity;

import com.bank.domain.common.valueobject.BaseId;
import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class BaseEntity<ID extends BaseId<?>> {
    protected ID id;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseEntity<?> that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
