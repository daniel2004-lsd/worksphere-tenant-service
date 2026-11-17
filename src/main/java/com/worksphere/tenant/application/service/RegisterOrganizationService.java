package com.worksphere.tenant.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worksphere.tenant.application.port.in.RegisterOrganizationCommand;
import com.worksphere.tenant.application.port.in.RegisterOrganizationUseCase;
import com.worksphere.tenant.application.port.out.OrganizationRepositoryPort;
import com.worksphere.tenant.application.port.out.OutboxEventRepositoryPort;
import com.worksphere.tenant.domain.exception.DuplicateSlugException;
import com.worksphere.tenant.domain.model.Organization;
import com.worksphere.tenant.domain.model.OutboxEvent;
import com.worksphere.tenant.domain.model.Slug;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterOrganizationService implements RegisterOrganizationUseCase {

    private final OrganizationRepositoryPort organizationRepository;
    private final OutboxEventRepositoryPort outboxEventRepository;
    private final ObjectMapper objectMapper;

    public RegisterOrganizationService(
            OrganizationRepositoryPort organizationRepository,
            OutboxEventRepositoryPort outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.organizationRepository = organizationRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Organization registerOrganization(RegisterOrganizationCommand command) {
        Slug slug = Slug.of(command.slug());

        if (organizationRepository.existsBySlug(slug)) {
            throw new DuplicateSlugException("An organization with slug '" + slug.getValue() + "' already exists.");
        }

        Organization organization = Organization.create(command.name(), slug.getValue(), command.adminEmail());
        Organization savedOrganization = organizationRepository.save(organization);

        // Transactional Outbox Pattern: Publish Event atomically
        String eventPayload = buildCloudEventPayload(savedOrganization);
        OutboxEvent outboxEvent = OutboxEvent.createPending(
                savedOrganization.getTenantId().getValue(),
                "ORGANIZATION",
                savedOrganization.getId().toString(),
                "worksphere.tenant.organization.created",
                eventPayload
        );
        outboxEventRepository.save(outboxEvent);

        return savedOrganization;
    }

    private String buildCloudEventPayload(Organization organization) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("id", organization.getId().toString());
            data.put("tenant_id", organization.getTenantId().getValue());
            data.put("name", organization.getName());
            data.put("slug", organization.getSlug().getValue());
            data.put("admin_email", organization.getAdminEmail());
            data.put("status", organization.getStatus().name());
            data.put("created_at", organization.getCreatedAt().toString());

            Map<String, Object> cloudEvent = new HashMap<>();
            cloudEvent.put("specversion", "1.0");
            cloudEvent.put("id", UUID.randomUUID().toString());
            cloudEvent.put("source", "/services/tenant-service");
            cloudEvent.put("type", "worksphere.tenant.organization.created");
            cloudEvent.put("time", Instant.now().toString());
            cloudEvent.put("datacontenttype", "application/json");
            cloudEvent.put("data", data);

            return objectMapper.writeValueAsString(cloudEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CloudEvent payload for Outbox", e);
        }
    }
}
