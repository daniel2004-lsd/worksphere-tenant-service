package com.worksphere.tenant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worksphere.tenant.application.port.in.RegisterOrganizationCommand;
import com.worksphere.tenant.application.port.in.RegisterOrganizationUseCase;
import com.worksphere.tenant.domain.exception.DuplicateSlugException;
import com.worksphere.tenant.domain.model.Organization;
import com.worksphere.tenant.infrastructure.adapter.in.web.OrganizationController;
import com.worksphere.tenant.infrastructure.adapter.in.web.dto.RegisterOrganizationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterOrganizationUseCase registerOrganizationUseCase;

    @Test
    @DisplayName("POST /api/v1/organizations returns 201 Created when payload is valid (AC-001)")
    void shouldReturn201WhenPayloadIsValid() throws Exception {
        RegisterOrganizationRequest request = new RegisterOrganizationRequest(
                "TechSolutions Inc",
                "techsolutions",
                "admin@techsolutions.com"
        );

        Organization created = Organization.create("TechSolutions Inc", "techsolutions", "admin@techsolutions.com");
        when(registerOrganizationUseCase.registerOrganization(any(RegisterOrganizationCommand.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TechSolutions Inc"))
                .andExpect(jsonPath("$.slug").value("techsolutions"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.tenantId").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/organizations returns 409 Conflict when slug already exists (AC-002)")
    void shouldReturn409WhenSlugAlreadyExists() throws Exception {
        RegisterOrganizationRequest request = new RegisterOrganizationRequest(
                "TechSolutions Inc",
                "techsolutions",
                "admin@techsolutions.com"
        );

        when(registerOrganizationUseCase.registerOrganization(any(RegisterOrganizationCommand.class)))
                .thenThrow(new DuplicateSlugException("An organization with slug 'techsolutions' already exists."));

        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @DisplayName("POST /api/v1/organizations returns 400 Bad Request when request body has invalid fields")
    void shouldReturn400WhenPayloadHasInvalidFields() throws Exception {
        RegisterOrganizationRequest invalidRequest = new RegisterOrganizationRequest(
                "",
                "INVALID SLUG!",
                "not-an-email"
        );

        mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
