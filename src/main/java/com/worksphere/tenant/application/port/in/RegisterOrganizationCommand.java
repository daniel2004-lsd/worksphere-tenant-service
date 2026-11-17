package com.worksphere.tenant.application.port.in;

public record RegisterOrganizationCommand(
        String name,
        String slug,
        String adminEmail
) {
    public RegisterOrganizationCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name must not be blank.");
        }
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("Organization slug must not be blank.");
        }
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin email must not be blank.");
        }
    }
}
