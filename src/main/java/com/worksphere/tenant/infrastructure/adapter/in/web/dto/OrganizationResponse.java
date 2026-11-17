package com.worksphere.tenant.infrastructure.adapter.in.web.dto;

import com.worksphere.tenant.domain.model.Organization;
import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String tenantId,
        String name,
        String slug,
        String adminEmail,
        String status,
        Instant createdAt
) {
    public static OrganizationResponse fromDomain(Organization org) {
        return new OrganizationResponse(
                org.getId(),
                org.getTenantId().getValue(),
                org.getName(),
                org.getSlug().getValue(),
                org.getAdminEmail(),
                org.getStatus().name(),
                org.getCreatedAt()
        );
    }
}
