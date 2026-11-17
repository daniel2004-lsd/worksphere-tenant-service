package com.worksphere.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worksphere.tenant.application.port.in.RegisterOrganizationCommand;
import com.worksphere.tenant.application.port.out.OrganizationRepositoryPort;
import com.worksphere.tenant.application.port.out.OutboxEventRepositoryPort;
import com.worksphere.tenant.application.service.RegisterOrganizationService;
import com.worksphere.tenant.domain.exception.DuplicateSlugException;
import com.worksphere.tenant.domain.exception.InvalidSlugException;
import com.worksphere.tenant.domain.model.Organization;
import com.worksphere.tenant.domain.model.OrganizationStatus;
import com.worksphere.tenant.domain.model.OutboxEvent;
import com.worksphere.tenant.domain.model.Slug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterOrganizationServiceTest {

    @Mock
    private OrganizationRepositoryPort organizationRepository;

    @Mock
    private OutboxEventRepositoryPort outboxEventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RegisterOrganizationService service;

    @BeforeEach
    void setUp() {
        service = new RegisterOrganizationService(organizationRepository, outboxEventRepository, objectMapper);
    }

    @Test
    @DisplayName("Should successfully register organization and persist transactional outbox event (AC-001)")
    void shouldRegisterOrganizationSuccessfully() {
        RegisterOrganizationCommand command = new RegisterOrganizationCommand(
                "TechSolutions Inc",
                "techsolutions",
                "admin@techsolutions.com"
        );

        when(organizationRepository.existsBySlug(any(Slug.class))).thenReturn(false);
        when(organizationRepository.save(any(Organization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Organization result = service.registerOrganization(command);

        assertNotNull(result);
        assertEquals("TechSolutions Inc", result.getName());
        assertEquals("techsolutions", result.getSlug().getValue());
        assertTrue(result.getTenantId().getValue().startsWith("ten_techsolutions_"));
        assertEquals(OrganizationStatus.ACTIVE, result.getStatus());

        verify(organizationRepository, times(1)).save(any(Organization.class));

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository, times(1)).save(outboxCaptor.capture());

        OutboxEvent capturedEvent = outboxCaptor.getValue();
        assertEquals("ORGANIZATION", capturedEvent.getAggregateType());
        assertEquals("worksphere.tenant.organization.created", capturedEvent.getEventType());
        assertEquals("PENDING", capturedEvent.getStatus());
        assertTrue(capturedEvent.getPayload().contains("techsolutions"));
    }

    @Test
    @DisplayName("Should reject registration when slug already exists with DuplicateSlugException (AC-002)")
    void shouldRejectDuplicateSlug() {
        RegisterOrganizationCommand command = new RegisterOrganizationCommand(
                "TechSolutions Clone",
                "techsolutions",
                "other@techsolutions.com"
        );

        when(organizationRepository.existsBySlug(Slug.of("techsolutions"))).thenReturn(true);

        assertThrows(DuplicateSlugException.class, () -> service.registerOrganization(command));

        verify(organizationRepository, never()).save(any());
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject invalid slug formats with InvalidSlugException (BR-01)")
    void shouldRejectInvalidSlugFormats() {
        RegisterOrganizationCommand invalidCommand = new RegisterOrganizationCommand(
                "Invalid Company",
                "Invalid_Slug!#$",
                "admin@test.com"
        );

        assertThrows(InvalidSlugException.class, () -> service.registerOrganization(invalidCommand));
    }
}
