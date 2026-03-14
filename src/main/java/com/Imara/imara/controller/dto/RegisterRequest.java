package com.Imara.imara.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Company name is required")
        @Size(max = 255)
        String companyName,

        @NotBlank(message = "Company email is required")
        @Email(message = "Company email must be valid")
        String companyEmail,

        @Size(max = 50)
        String companyPhone,

        @NotBlank(message = "Full name is required")
        @Size(max = 255)
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}
