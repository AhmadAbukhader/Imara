package com.Imara.imara.dto;

import com.Imara.imara.model.Building;

import java.time.Instant;
import java.util.UUID;

public record BuildingResponse(
        UUID id,
        UUID companyId,
        String name,
        String address,
        String city,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
    public static BuildingResponse from(Building building) {
        return new BuildingResponse(
                building.getId(),
                building.getCompanyId(),
                building.getName(),
                building.getAddress(),
                building.getCity(),
                building.getIsActive(),
                building.getCreatedAt(),
                building.getUpdatedAt()
        );
    }
}
