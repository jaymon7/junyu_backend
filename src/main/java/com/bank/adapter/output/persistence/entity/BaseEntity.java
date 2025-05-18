package com.bank.adapter.output.persistence.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseEntity<ID> {
    @Id
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
