package com.Imara.imara.controller.dto;

import jakarta.validation.constraints.Size;

public record UpdateBuildingRequest(
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @Size(max = 255)
        String address,

        @Size(max = 100)
        String city,

        Boolean isActive
) {}
