package com.Imara.imara.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBuildingRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 255)
        String name,

        @Size(max = 255)
        String address,

        @Size(max = 100)
        String city,

        Boolean isActive
) {}
