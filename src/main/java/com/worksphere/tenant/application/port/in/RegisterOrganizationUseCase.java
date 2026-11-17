package com.worksphere.tenant.application.port.in;

import com.worksphere.tenant.domain.model.Organization;

public interface RegisterOrganizationUseCase {
    Organization registerOrganization(RegisterOrganizationCommand command);
}
