package com.worksphere.tenant.application.port.out;

import com.worksphere.tenant.domain.model.Organization;
import com.worksphere.tenant.domain.model.Slug;
import com.worksphere.tenant.domain.model.TenantId;
import java.util.Optional;

public interface OrganizationRepositoryPort {
    Organization save(Organization organization);
    Optional<Organization> findBySlug(Slug slug);
    Optional<Organization> findByTenantId(TenantId tenantId);
    boolean existsBySlug(Slug slug);
}
