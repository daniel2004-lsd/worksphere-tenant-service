package com.worksphere.tenant.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class OutboxEvent {

    private final UUID id;
    private final String tenantId;
    private final String aggregateType;
    private final String aggregateId;
    private final String eventType;
    private final String payload;
    private String status;
    private int retryCount;
    private String lastError;
    private final Instant createdAt;
    private Instant publishedAt;

    public OutboxEvent(
            UUID id,
            String tenantId,
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload,
            String status,
            int retryCount,
            String lastError,
            Instant createdAt,
            Instant publishedAt
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.tenantId = Objects.requireNonNull(tenantId, "TenantId must not be null");
        this.aggregateType = Objects.requireNonNull(aggregateType, "AggregateType must not be null");
        this.aggregateId = Objects.requireNonNull(aggregateId, "AggregateId must not be null");
        this.eventType = Objects.requireNonNull(eventType, "EventType must not be null");
        this.payload = Objects.requireNonNull(payload, "Payload must not be null");
        this.status = status != null ? status : "PENDING";
        this.retryCount = retryCount;
        this.lastError = lastError;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.publishedAt = publishedAt;
    }

    public static OutboxEvent createPending(
            String tenantId,
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload
    ) {
        return new OutboxEvent(
                UUID.randomUUID(),
                tenantId,
                aggregateType,
                aggregateId,
                eventType,
                payload,
                "PENDING",
                0,
                null,
                Instant.now(),
                null
        );
    }

    public void markPublished() {
        this.status = "PUBLISHED";
        this.publishedAt = Instant.now();
    }

    public void markFailed(String error) {
        this.retryCount++;
        this.lastError = error;
        if (this.retryCount >= 5) {
            this.status = "FAILED";
        }
    }

    public UUID getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
