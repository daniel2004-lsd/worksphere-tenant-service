package com.worksphere.tenant.infrastructure.adapter.out.persistence;

import com.worksphere.tenant.application.port.out.OrganizationRepositoryPort;
import com.worksphere.tenant.domain.model.Organization;
import com.worksphere.tenant.domain.model.OrganizationStatus;
import com.worksphere.tenant.domain.model.Slug;
import com.worksphere.tenant.domain.model.TenantId;
import com.worksphere.tenant.infrastructure.adapter.out.persistence.entity.OrganizationJpaEntity;
import com.worksphere.tenant.infrastructure.adapter.out.persistence.repository.SpringDataOrganizationRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OrganizationPersistenceAdapter implements OrganizationRepositoryPort {

    private final SpringDataOrganizationRepository repository;

    public OrganizationPersistenceAdapter(SpringDataOrganizationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Organization save(Organization organization) {
        OrganizationJpaEntity entity = toEntity(organization);
        OrganizationJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Organization> findBySlug(Slug slug) {
        return repository.findBySlug(slug.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<Organization> findByTenantId(TenantId tenantId) {
        return repository.findByTenantId(tenantId.getValue()).map(this::toDomain);
    }

    @Override
    public boolean existsBySlug(Slug slug) {
        return repository.existsBySlug(slug.getValue());
    }

    private OrganizationJpaEntity toEntity(Organization domain) {
        return OrganizationJpaEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId().getValue())
                .name(domain.getName())
                .slug(domain.getSlug().getValue())
                .adminEmail(domain.getAdminEmail())
                .status(domain.getStatus().name())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Organization toDomain(OrganizationJpaEntity entity) {
        return new Organization(
                entity.getId(),
                TenantId.of(entity.getTenantId()),
                entity.getName(),
                Slug.of(entity.getSlug()),
                entity.getAdminEmail(),
                OrganizationStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
