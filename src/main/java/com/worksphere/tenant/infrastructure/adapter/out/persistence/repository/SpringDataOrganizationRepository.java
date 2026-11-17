package com.worksphere.tenant.infrastructure.adapter.out.persistence.repository;

import com.worksphere.tenant.infrastructure.adapter.out.persistence.entity.OrganizationJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrganizationRepository extends JpaRepository<OrganizationJpaEntity, UUID> {
    Optional<OrganizationJpaEntity> findBySlug(String slug);
    Optional<OrganizationJpaEntity> findByTenantId(String tenantId);
    boolean existsBySlug(String slug);
}
