package com.worksphere.tenant.infrastructure.adapter.out.persistence.repository;

import com.worksphere.tenant.infrastructure.adapter.out.persistence.entity.OutboxEventJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataOutboxEventRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    @Query("SELECT e FROM OutboxEventJpaEntity e WHERE e.status = 'PENDING' ORDER BY e.createdAt ASC")
    List<OutboxEventJpaEntity> findPendingEvents(Pageable pageable);
}
