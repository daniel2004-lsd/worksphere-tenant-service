package com.worksphere.tenant.infrastructure.adapter.in.web;

import com.worksphere.tenant.application.port.in.RegisterOrganizationCommand;
import com.worksphere.tenant.application.port.in.RegisterOrganizationUseCase;
import com.worksphere.tenant.domain.model.Organization;
import com.worksphere.tenant.infrastructure.adapter.in.web.dto.OrganizationResponse;
import com.worksphere.tenant.infrastructure.adapter.in.web.dto.RegisterOrganizationRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final RegisterOrganizationUseCase registerOrganizationUseCase;

    public OrganizationController(RegisterOrganizationUseCase registerOrganizationUseCase) {
        this.registerOrganizationUseCase = registerOrganizationUseCase;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> registerOrganization(
            @Valid @RequestBody RegisterOrganizationRequest request
    ) {
        RegisterOrganizationCommand command = new RegisterOrganizationCommand(
                request.name(),
                request.slug(),
                request.adminEmail()
        );

        Organization created = registerOrganizationUseCase.registerOrganization(command);
        OrganizationResponse response = OrganizationResponse.fromDomain(created);

        return ResponseEntity
                .created(URI.create("/api/v1/organizations/" + created.getId()))
                .body(response);
    }
}
