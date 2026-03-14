package com.Imara.imara.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @Email(message = "Email must be valid")
        @Size(max = 255)
        String email,

        @Size(max = 50)
        String phone,

        Boolean isActive
) {}
