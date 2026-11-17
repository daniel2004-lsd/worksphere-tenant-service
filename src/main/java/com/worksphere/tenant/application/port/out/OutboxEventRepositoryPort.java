package com.worksphere.tenant.application.port.out;

import com.worksphere.tenant.domain.model.OutboxEvent;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepositoryPort {
    OutboxEvent save(OutboxEvent event);
    List<OutboxEvent> findPendingEvents(int limit);
    void updateStatus(UUID eventId, String status);
}
