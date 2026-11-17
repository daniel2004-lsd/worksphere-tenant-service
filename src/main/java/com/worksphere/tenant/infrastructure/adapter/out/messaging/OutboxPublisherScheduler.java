package com.worksphere.tenant.infrastructure.adapter.out.messaging;

import com.worksphere.tenant.application.port.out.OutboxEventRepositoryPort;
import com.worksphere.tenant.domain.model.OutboxEvent;
import com.worksphere.tenant.infrastructure.adapter.out.persistence.rls.TenantContextHolder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisherScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherScheduler.class);

    private final OutboxEventRepositoryPort outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${worksphere.rabbitmq.exchange.tenant:worksphere.tenant.events}")
    private String tenantExchangeName;

    public OutboxPublisherScheduler(
            OutboxEventRepositoryPort outboxEventRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${worksphere.outbox.poll-interval-ms:3000}")
    public void processOutboxEvents() {
        try {
            // Set SYSTEM context to bypass tenant partition during background event processing
            TenantContextHolder.setTenantId(TenantContextHolder.SYSTEM_TENANT);

            List<OutboxEvent> pendingEvents = outboxEventRepository.findPendingEvents(20);
            if (pendingEvents.isEmpty()) {
                return;
            }

            log.info("Processing {} pending Outbox events...", pendingEvents.size());

            for (OutboxEvent event : pendingEvents) {
                try {
                    String routingKey = event.getEventType();
                    rabbitTemplate.convertAndSend(tenantExchangeName, routingKey, event.getPayload());

                    outboxEventRepository.updateStatus(event.getId(), "PUBLISHED");
                    log.info("Dispatched Outbox event ID={} type='{}' to exchange='{}'",
                            event.getId(), routingKey, tenantExchangeName);
                } catch (Exception e) {
                    log.error("Failed to publish Outbox event ID={}: {}", event.getId(), e.getMessage());
                    outboxEventRepository.updateStatus(event.getId(), "FAILED");
                }
            }
        } finally {
            TenantContextHolder.clear();
        }
    }
}
