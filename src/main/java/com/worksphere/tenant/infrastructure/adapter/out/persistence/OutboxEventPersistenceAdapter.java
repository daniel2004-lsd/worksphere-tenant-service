package com.worksphere.tenant.infrastructure.adapter.out.persistence;

import com.worksphere.tenant.application.port.out.OutboxEventRepositoryPort;
import com.worksphere.tenant.domain.model.OutboxEvent;
import com.worksphere.tenant.infrastructure.adapter.out.persistence.entity.OutboxEventJpaEntity;
import com.worksphere.tenant.infrastructure.adapter.out.persistence.repository.SpringDataOutboxEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventPersistenceAdapter implements OutboxEventRepositoryPort {

    private final SpringDataOutboxEventRepository repository;

    public OutboxEventPersistenceAdapter(SpringDataOutboxEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventJpaEntity entity = toEntity(event);
        OutboxEventJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<OutboxEvent> findPendingEvents(int limit) {
        return repository.findPendingEvents(PageRequest.of(0, limit)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void updateStatus(UUID eventId, String status) {
        repository.findById(eventId).ifPresent(entity -> {
            entity.setStatus(status);
            if ("PUBLISHED".equals(status)) {
                entity.setPublishedAt(Instant.now());
            }
            repository.save(entity);
        });
    }

    private OutboxEventJpaEntity toEntity(OutboxEvent domain) {
        return OutboxEventJpaEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .aggregateType(domain.getAggregateType())
                .aggregateId(domain.getAggregateId())
                .eventType(domain.getEventType())
                .payload(domain.getPayload())
                .status(domain.getStatus())
                .retryCount(domain.getRetryCount())
                .lastError(domain.getLastError())
                .createdAt(domain.getCreatedAt())
                .publishedAt(domain.getPublishedAt())
                .build();
    }

    private OutboxEvent toDomain(OutboxEventJpaEntity entity) {
        return new OutboxEvent(
                entity.getId(),
                entity.getTenantId(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getPayload(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getLastError(),
                entity.getCreatedAt(),
                entity.getPublishedAt()
        );
    }
}
