package com.worksphere.tenant.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Organization {

    private final UUID id;
    private final TenantId tenantId;
    private String name;
    private final Slug slug;
    private String adminEmail;
    private OrganizationStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Organization(
            UUID id,
            TenantId tenantId,
            String name,
            Slug slug,
            String adminEmail,
            OrganizationStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId must not be null");
        this.name = Objects.requireNonNull(name, "Name must not be null").trim();
        this.slug = Objects.requireNonNull(slug, "Slug must not be null");
        this.adminEmail = Objects.requireNonNull(adminEmail, "AdminEmail must not be null").trim();
        this.status = status != null ? status : OrganizationStatus.ACTIVE;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public static Organization create(String name, String slugStr, String adminEmail) {
        Slug slug = Slug.of(slugStr);
        TenantId tenantId = TenantId.generate(slug);
        Instant now = Instant.now();
        return new Organization(
                UUID.randomUUID(),
                tenantId,
                name,
                slug,
                adminEmail,
                OrganizationStatus.ACTIVE,
                now,
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public Slug getSlug() {
        return slug;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name cannot be empty.");
        }
        this.name = newName.trim();
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        this.status = OrganizationStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.status = OrganizationStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }
}
