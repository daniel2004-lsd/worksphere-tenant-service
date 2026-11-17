package com.worksphere.tenant.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterOrganizationRequest(
        @NotBlank(message = "Organization name is required")
        @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
        String name,

        @NotBlank(message = "Slug is required")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug can only contain lowercase letters, numbers, and hyphens")
        @Size(min = 3, max = 64, message = "Slug must be between 3 and 64 characters")
        String slug,

        @NotBlank(message = "Admin email is required")
        @Email(message = "Must be a valid email address")
        String adminEmail
) {}
