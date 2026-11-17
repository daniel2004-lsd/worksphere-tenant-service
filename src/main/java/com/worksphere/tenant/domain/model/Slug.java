package com.worksphere.tenant.domain.model;

import com.worksphere.tenant.domain.exception.InvalidSlugException;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Slug {

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9-]+$");
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 64;

    private final String value;

    public Slug(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidSlugException("Slug cannot be null or empty.");
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
            throw new InvalidSlugException(
                "Slug length must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters."
            );
        }
        if (!SLUG_PATTERN.matcher(normalized).matches()) {
            throw new InvalidSlugException(
                "Slug '" + value + "' contains invalid characters. Only lowercase alphanumeric and hyphens are allowed."
            );
        }
        this.value = normalized;
    }

    public static Slug of(String value) {
        return new Slug(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slug slug = (Slug) o;
        return Objects.equals(value, slug.value);
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
