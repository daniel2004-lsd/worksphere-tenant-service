package com.worksphere.tenant.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class TenantId {

    private final String value;

    public TenantId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TenantId cannot be null or empty.");
        }
        this.value = value.trim();
    }

    public static TenantId generate(Slug slug) {
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return new TenantId("ten_" + slug.getValue() + "_" + randomSuffix);
    }

    public static TenantId of(String value) {
        return new TenantId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantId tenantId = (TenantId) o;
        return Objects.equals(value, tenantId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
